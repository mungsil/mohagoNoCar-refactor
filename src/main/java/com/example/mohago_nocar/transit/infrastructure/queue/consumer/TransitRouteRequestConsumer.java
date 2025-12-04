package com.example.mohago_nocar.transit.infrastructure.queue.consumer;

import com.example.mohago_nocar.global.common.exception.Status;
import com.example.mohago_nocar.global.util.ObjectMapperUtil;
import com.example.mohago_nocar.plan.application.v2.TravelCoursePlanNotifyService;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;
import com.example.mohago_nocar.transit.infrastructure.queue.batch.TransitRouteRequest;
import com.example.mohago_nocar.transit.infrastructure.queue.batch.BatchStatus;
import com.example.mohago_nocar.transit.infrastructure.error.code.OdsayErrorCode;
import com.example.mohago_nocar.transit.infrastructure.error.exception.ODsayRouteException;
import com.example.mohago_nocar.transit.infrastructure.route.TransitRouteApiAdapter;
import io.github.bucket4j.*;
import io.lettuce.core.RedisBusyException;
import io.lettuce.core.RedisConnectionException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// todo readOffset 설정 확인
@Component
@RequiredArgsConstructor
@Slf4j
public class TransitRouteRequestConsumer implements StreamListener<String, ObjectRecord<String, String>> {

    @Value("${redis.streams.odsay.main}")
    private String streamKey;

    private final String consumerGroup = "processors";

    private StreamMessageListenerContainer<String, ObjectRecord<String, String>> listenerContainer;

    private final StringRedisTemplate stringRedisTemplate;
    private final TravelCoursePlanNotifyService travelCoursePlanNotifyService;
    private final ObjectMapperUtil objectMapperUtil;
    private final TransitRouteApiAdapter transitRouteApiAdapter;

    private final ExecutorService virtualThreadPool = Executors.newVirtualThreadPerTaskExecutor();

    private Bucket bucket;

    @PostConstruct
    public void init() {
        bucket = createBucket();

        // Consumer Group 설정
        createStreamConsumerGroupIfNotExists(streamKey, consumerGroup);

        // StreamMessageListenerContainer 설정
        this.listenerContainer = StreamMessageListenerContainer.create(
                stringRedisTemplate.getConnectionFactory(),
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .targetType(String.class)
                        .pollTimeout(Duration.ofSeconds(3))
                        .batchSize(1)
                        .errorHandler(t -> log.info(t.getMessage())) // todo 로깅 및 알림
                        .build()
        );

        listenerContainer.register(StreamMessageListenerContainer.StreamReadRequest
                        .builder(StreamOffset.create(streamKey, ReadOffset.lastConsumed()))
                        .cancelOnError(throwable -> false)
                        .consumer(Consumer.from(this.consumerGroup, "processors-1"))
                        .autoAcknowledge(false).build()
                , this
        );

/*        int parallelListener = 2;
        for (int i = 1; i <= parallelListener; i++) {
            String consumerName = "processor-" + i;

            listenerContainer.register(StreamMessageListenerContainer.StreamReadRequest
                            .builder(StreamOffset.create(streamKey, ReadOffset.lastConsumed()))
                            .cancelOnError(throwable -> false)
                            .consumer(Consumer.from(this.consumerGroup, consumerName))
                            .autoAcknowledge(false).build()
                    , this
            );
        }*/

        // Redis listen 시작
        this.listenerContainer.start();
    }

/*    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // todo 로깅 + 알림 : 현재 스레드를 인터럽트하는 코드를 작성해두지 않음 -> 원인 파악 후 처리
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        asyncProcessRequest(message);
    }*/



    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        try {
            bucket.asBlocking().consume(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        asyncProcessRequest(message);
    }

    private void asyncProcessRequest(ObjectRecord<String, String> message) {
        TransitRouteRequest request = objectMapperUtil.readValue(message.getValue(), TransitRouteRequest.class);

        // todo 이미 실패한 배치의 요청인지 확인 -> 이미 실패한 배치 요청이라면 throw ex
        CompletableFuture.supplyAsync(() -> fetchTransitRouteByApiCalling(request), virtualThreadPool)
                .handle((route, ex) -> {
                    if (ex != null) {
                        // if retryable exception
                        // todo 재시도 with 지수 백오프 구현 방법 -> pending 리스트에 그대로 두기
                        if (isTooManyRequestsEx(ex)) {
                            // Using retry count of request, consider follower two options
                            // 1. send to DLQ if <- exceed enable auto retry count
                            // 2. add to sorted set <- else
                            return null;
                        }

                        // [로깅, 개발자 알림], 배치 실패 처리
                        // 개발자 알림은 why, how?
                        // why?
                        // 예상치 못한 예외 (네트웤, 429, external api unexpected ex)에 대한 대응이 필요하기 때문임
                        // how?
                        // 1. 로그 기반 일정 시간 간격: 예외 발생 사실 확인 -> 로깅 -> 정기적 알림 전송
                        // 장점: 예상 가능한 알림 전송량 + 예외가 다량 발생해도 통계적으로 확인 가능
                        // 단점: 실시간성이 떨어짐. 실시간으로 대처해야하는 에러가 있다면?
                        // -> '실시간 대처가 필요한' 에러란?
                        // -> 모르겠음.
                        // 2. 예외 발생 시 실시간 알림 전송
                        // 장점: 실시간으로 예외 지각 가능
                        // 단점: 알림이 많이 와서 파악이 어려울 가능성 있음 (근데 현재는 아님)
                        //

                        // todo need a block circuit
                        if (ex instanceof ODsayRouteException odsayEx) {
                            Status status = odsayEx.getStatus();
                            // 빠르게 응답을 줘야함 -> 배치 실패 처리 후 로깅
                            // todo cause로 api 키나 ip 문제 인지 확인
                            if (status instanceof OdsayErrorCode errorCode) {
                                errorCode.isServerError();
                            }
                            // 실패 처리 -> 바로 알림 주는게 조은뎅
                        }

                        if (ex instanceof RestClientException clientEx) {
                            // 재시도하는 거 어때욤? 네트워크 에러잖아...
                            // ah, I was thought I should only add a circuit breaker,
                            // but now I'm think I need both.
                            // 아 이거!! 원인이 네트워크 에러만 있는게 아니었음.
                        }

//                        todo lost update 발생 해결 -> 루아스크립트 사용.
//                        TransitRouteBatchExecution batchExecution = batchExecutionRepository.findByExecutionId(request.getBatchId());
//                        batchExecution.fail((Exception) ex); // 배치 상태 실패로 변경 - 가장 먼저 발생한 에러만 기록
//                        batchExecutionRepository.save(batchExecution);
//                        // 실패한 request dlq로 이동
//                        onAfterFailure.run(); // entry ack & delete

                        // * 실패 알림은 워커 스레드가 수행할거임. <- 배치 상태 fail 인거 확인할거임.
                    }

                    // todo 멱등성 테스트
                    else {
                        BatchStatus batchStatus = processWhenRequestSuccess(request.getBatchId(), route, request.getSequence());
                        if (batchStatus == BatchStatus.COMPLETED) {
                            // 결과 rdb 저장 - notification id + batch id가 기준이 될 것임.
                            saveCompletedTransitRoute(request.getBatchId());

                            // 알림 전송
                            travelCoursePlanNotifyService.sendSuccessNotification(request.getBatchId());
                        }

                        // 여기서 레디스 서버 크러쉬되면 중복 처리 발생
                        ackAndDeleteEntry(message.getId());
                    }

                    return null;
                }).exceptionally(throwable -> {
                    log.error("Error processing request", throwable);


                    // 2. 네트워크 혼잡 예외

                    // 3. 레디스 인프라 예외
                    // 어떤 예외가 있는지 몰라욤. 공부 필요해염.
                    // 레디스 서버가 내려가는 경우 -> 1. 정상화될때까지 롱폴링 2. RDB

                    // 4. 비즈니스 로직 예외.
                    // 존재하지 않는 배치 아이디 예외 같은거.
                    // > 예상하지 못한 상황 < 실패 처리하고 디스코드 알림, 로깅하기.

                    // etc. 그 외 내가 모르는 예외
                    // 일단 DLQ로 전송, 디스코드 알림, 로깅하기.
                    return null;
                });
    }

    private Bucket createBucket() {
        int permitAPICallNumPerSec = 10;
        Bandwidth limit = BandwidthBuilder.builder().capacity(permitAPICallNumPerSec)
                .refillIntervally(permitAPICallNumPerSec, Duration.ofSeconds(1))
                .initialTokens(0)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private boolean isTooManyRequestsEx(Throwable ex) {
        if (ex instanceof ODsayRouteException odsayEx) {
            Status status = odsayEx.getStatus();
            if (status instanceof OdsayErrorCode errorCode) {
                if (errorCode.isTooManyRequests()) {
                    return true;
                }

                errorCode.isServerError(); // 로깅 with request , DLQ 전송 // 개발자 알림

                    // 아래는 공통적으로 유저 (실패) 알림 전송 // 개발자 알림
                errorCode.isUnExpectedError(); // 로깅 with request,

            // 그 외: 로깅도 안함.
            } else {
                log.warn("UnExpected Status exists : {}", status);
                // 로깅 후 DLQ 전송 // 개발자 알림
            }
        }

        // 레디스 크러쉬
        // 서버가 죽으면 '재시도' 하는 것보다 '차단'하는 게 이득이 아닐까?
        // 해당 사항은 레디스 서버 크러쉬임

        // 네트워크 에러
        // 재시도하는게 좋겠지만, 이거 네트워크 혼잡 때문이다! 라고 에러 식별할 수 있는 방법을 모름.
        // 이것도 서킷 브레이커 패턴을 쓰면 어떨까

        if (ex instanceof RedisConnectionException connEX){
            // 재시도 or 실패
        }

        return false;
    }

    private void ackAndDeleteEntry(RecordId recordId) {
        String lua = """
            local stream = ARGV[1]
            local group = ARGV[2]
            local id = ARGV[3]
            local acked = redis.call('XACK', stream, group, id)
            local deleted = redis.call('XDEL', stream, id)
            return {acked, deleted}
        """;

        DefaultRedisScript<List> script = new DefaultRedisScript<>(lua, List.class);
        List<Long> executed = stringRedisTemplate.execute(
                script,
                List.of(),                       // KEYS 없음
                streamKey,                       // ARGV[1]
                consumerGroup,                   // ARGV[2]
                recordId.getValue()       // ARGV[3]
        );

        Long acked = executed.get(0);
        Long deleted = executed.get(1);

        if (acked == 0 || deleted == 0) {
            log.warn("[Redis] ackAndDeleteEntry: ack={}, del={}, id={}", acked, deleted, recordId.getValue());
        }

    }

    private void saveCompletedTransitRoute(String batchId) {

    }

    // todo sequence 구분자는 batch 도메인에게 물어봐야한다. 필드명도...
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
                "completedSequences", // 필드명
                "totalCount", // 필드명
                1
        );

        return BatchStatus.valueOf(result);
    }

    // todo 여기서 발생 가능한 예외 정리
    public TransitRoute fetchTransitRouteByApiCalling(TransitRouteRequest request) {
        return transitRouteApiAdapter.getTransitRouteBetweenLocations(request.getOrigin(), request.getDestination());
    }

    private void createStreamConsumerGroupIfNotExists(String streamKey, String consumerGroup) {
        try {
            stringRedisTemplate.opsForStream()
                    .createGroup(streamKey, ReadOffset.from("0"), consumerGroup);
            log.info("Consumer group {} created", consumerGroup);
        } catch (RedisSystemException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RedisBusyException busyException) {
                log.info(busyException.getMessage());
                return;
            }

            throw new RuntimeException(
                    "Unexpected error while creating consumer group: ", cause); // todo convert to 커스텀 ex
        }
    }
    
}
