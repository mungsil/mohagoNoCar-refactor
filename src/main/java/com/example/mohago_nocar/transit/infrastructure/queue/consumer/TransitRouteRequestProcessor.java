package com.example.mohago_nocar.transit.infrastructure.queue.consumer;

import com.example.mohago_nocar.global.util.ObjectMapperUtil;
import com.example.mohago_nocar.plan.application.v2.TravelCoursePlanNotifyService;
import com.example.mohago_nocar.transit.infrastructure.queue.batch.BatchStatus;
import com.example.mohago_nocar.transit.infrastructure.queue.batch.TransitRouteRequest;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;
import com.example.mohago_nocar.transit.infrastructure.queue.batch.TransitRouteBatchExecution;
import com.example.mohago_nocar.transit.infrastructure.queue.batch.TransitRouteBatchExecutionRepository;
import com.example.mohago_nocar.transit.domain.repository.TransitRouteRepository;
import com.example.mohago_nocar.transit.infrastructure.route.TransitRouteApiAdapter;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * QueueRelay: send item from A to B queue
 * RateLimitedConsumer: consume item by limited rate
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TransitRouteRequestProcessor {

    private final TransitRouteApiAdapter transitRouteApiAdapter;
    private final TransitRouteBatchExecutionRepository batchExecutionRepository;
    private final ObjectMapperUtil objectMapperUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final TransitRouteRepository transitRouteRepository;
    private final TravelCoursePlanNotifyService travelCoursePlanNotifyService;

    private final PriorityBlockingQueue<PrioritizedTransitRouteRequest> queue = new PriorityBlockingQueue<>();

    // 우선 순위를 위해 진입 순서 부여
    private final AtomicLong insertionOrderCounter = new AtomicLong(0);

    private final ExecutorService virtualThreadPool = Executors.newVirtualThreadPerTaskExecutor();

    private RateLimiter rateLimiter;
    private Runnable onAfterSuccess;
    private Runnable onAfterFailure;

    public void receive(ObjectRecord<String, String> msg) {
        this.queue.add(PrioritizedTransitRouteRequest.builder()
                .value(objectMapperUtil.readValue(msg.getValue(), TransitRouteRequest.class))
                .insertionOrder(insertionOrderCounter.addAndGet(1)).build());
    }

    /**
     * 워커 스레드를 시작합니다.
     */
    public void startWorker() {
        Thread thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                newDoWork();
            }
            // Q: 자원 정리 로직 추가해야할까?
            // 정리해야될 자원은 뭐가 있을까
        });
        thread.start();
    }

    // todo executor service를 이용한 수행. -> 남은 작업을 완료할 수 있도록 대기하고 싶음, 미래에 소비자가 늘면 관리가 복잡해짐
    // rate limiting만 수행하고 그 외 작업은 비동기 처리.
    // 1. api call with "rate limit"
    // 2. process result
    public void newDoWork() {
        // 다음 과정을 executorservice에 submit
        // (동기) rate limit 대기
        // (비동기) CompletableFuture 사용하여 api call 및 handle
        TransitRouteRequest request;
        try {
            request = queue.take().getValue(); // 블로킹
        } catch (InterruptedException e) {
            // todo deadMsgProducer.produce
            onAfterFailure.run();
            return;
        }
//      rateLimiter.acquirePermission();
//      thenAccept: api Call
//       handle: process // 여기서 블로킹 발생함.
        /*
        문제:
         */
        rateLimiter.executeRunnable(() -> asyncProcessRequest(request));
    }

    private void asyncProcessRequest(TransitRouteRequest request) {
        CompletableFuture.supplyAsync(() -> fetchTransitRouteByApiCalling(request), virtualThreadPool)
                .handle((route, ex) -> {
                    if (ex != null) { // 여기서 발생하는 예외가 다인가?
                        // todo lost update 발생 해결 -> 루아스크립트 사용.
                        TransitRouteBatchExecution batchExecution = batchExecutionRepository.findByExecutionId(request.getBatchId());
                        batchExecution.fail((Exception) ex); // 배치 상태 실패로 변경 - 가장 먼저 발생한 에러만 기록
                        batchExecutionRepository.save(batchExecution);
                        // 실패한 request dlq로 이동
                        onAfterFailure.run(); // entry ack & delete
                    }else {
                        BatchStatus batchStatus = processWhenRequestSuccess(request.getBatchId(), route, request.getSequence());
                        if (batchStatus == BatchStatus.COMPLETED) {
                            // 결과 rdb 저장 - notification id + batch id가 기준이 될 것임.
                            saveCompletedTransitRoute(request.getBatchId());

                            // 알림 전송
                            travelCoursePlanNotifyService.sendSuccessNotification(request.getBatchId());
                        }
                        onAfterSuccess.run();
                    }

                    return null;
                });
    }

    private void saveCompletedTransitRoute(String batchId) {

    }

    private BatchStatus processWhenRequestSuccess(String batchId, TransitRoute route, int sequenceInBatch) {
        String lua =
            """
                local zsetKey = KEYS[1] -- Sorted Set 키 (경로 결과 저장용)
                local hashKey = KEYS[2] -- Hash 키 (배치 메타데이터 저장용)
                local score = tonumber(ARGV[1]) -- Sorted Set의 점수값 (경로의 순서(시퀀스) 저장용)
                local member = ARGV[2] -- Sorted Set에 저장할 멤버(경로 데이터)
                local completedSeqsField = ARGV[3] -- 완료된 시퀀스 목록 필드명
                local totalField = ARGV[4] -- 전체 시퀀스 목록 필드명
                local newCompletedSeq = ARGV[5] -- 완료된 시퀀스
        
                -- 1. API 호출 결과(대중교통 경로) 저장
                local added = redis.call('ZADD', zsetKey, score, member)
        
                local function updateCompletedSeqList(hashKey, completedSeqsField, newCompletedSeq)
                    local current = redis.call('HGET', hashKey, completedSeqsField)
        
                    -- 시퀀스 목록이 비어있는 경우
                    if not current or current == '' then
                        redis.call('HSET', hashKey, completedSeqsField, newCompletedSeq)
                        return 1
                    end
        
                    local alreadyExists = false
                    local existedCount = 0
                    for seq in string.gmatch(current, '([^,]+)') do
                        existedCount = existedCount + 1
                        if seq == newCompletedSeq then
                            alreadyExists = true
                        end
                    end
                    
                    if alreadyExists then
                        return existedCount
                    end
                    
                    -- 완료 목록에 시퀀스 업데이트
                    local updated = current .. ',' .. newCompletedSeq
                    redis.call('HSET', hashKey, completedSeqsField, updated)
                    return existedCount + 1
                end
        
                -- 2. 시퀀스 완료 처리
                local completedCount = updateCompletedSeqList(hashKey, completedSeqsField, newCompletedSeq)
        
                -- 3. 배치 완료 여부 확인
                local total = redis.call('HGET', hashKey, totalField)
                if tostring(completedCount) == tostring(total) then
                    redis.call('HSET', hashKey, 'status', 'completed')
                end
        
                -- 4. 배치 상태 반환
                local batchStatus = redis.call('HGET', hashKey, 'status')
                return batchStatus
            """;
        DefaultRedisScript<String> script = new DefaultRedisScript<>(lua, String.class);

        // todo 키 별도 관리
        String sortedSetKey = "transit:routes" + batchId;
        String batchExecutionKey = "batch:" + batchId;
        String result = stringRedisTemplate.execute(
                script,
                List.of(sortedSetKey, batchExecutionKey),
                sequenceInBatch,
                route,
                "completedCount", // 필드명
                "totalCount", // 필드명
                1
        );

        return BatchStatus.valueOf(result);
    }

    // todo 여기서 발생 가능한 예외 정리
    public TransitRoute fetchTransitRouteByApiCalling(TransitRouteRequest request) {
        return transitRouteApiAdapter.getTransitRouteBetweenLocations(request.getOrigin(), request.getDestination());
    }

    @PostConstruct
    public void init() {
        rateLimiter = initializeRateLimiter().rateLimiter("odsay api rate limiter");
        startWorker();
    }

    private RateLimiterRegistry initializeRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMillis(200))
                .limitForPeriod(1)
                .timeoutDuration(Duration.ofSeconds(20))
                .build();

        return RateLimiterRegistry.of(config);
    }

    public void onAfterSuccess(Runnable action) {
        onAfterSuccess = action;
    }

    public void onAfterFailure(Runnable action) {
        onAfterFailure = action;
    }

}