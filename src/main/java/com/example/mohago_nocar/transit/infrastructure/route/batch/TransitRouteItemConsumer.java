package com.example.mohago_nocar.transit.infrastructure.route.batch;

import com.example.mohago_nocar.transit.domain.model.OdsayApiRequest;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;
import com.example.mohago_nocar.transit.infrastructure.route.TransitRouteApiAdapter;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * 아이템을 폴링하여 내부적인 큐로 재전송합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Getter
public class TransitRouteItemConsumer {

    private final RedisTemplate<String, Object> redisTemplateWithObj;
    private final TransitRouteApiAdapter transitRouteApiAdapter;

    private static final String STREAM_KEY = "odsay-api-request";
    private static final String CONSUMER_GROUP = "odsay-api-consumer";
    private static final String CONSUMER_NAME = "consumer-1";

    private final PriorityBlockingQueue<ObjectRecord<String, OdsayApiRequest>> queue = new PriorityBlockingQueue<>();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private RateLimiter rateLimiter;

    @PostConstruct
    public void init() {
        log.info("Initializing TransitRouteItemConsumer");
        // 소비자 그룹 생성
        try {
            redisTemplateWithObj
                    .opsForStream()
                    .createGroup(STREAM_KEY, ReadOffset.lastConsumed(), CONSUMER_GROUP);
            log.info("Consumer group {} created", CONSUMER_GROUP);
        } catch (Exception e) {
            log.error("Error initializing TransitRouteItemConsumer", e);
            log.info("Consumer group {} already exists or error creating: {}", CONSUMER_GROUP, e.getMessage());
        }

        // 속도 제한 설정
        rateLimiter = initializeRateLimiter().rateLimiter("o");

//        startWorker();
    }

    /**
     * 1.retry = 재시도 큐 확인 > 일정 시간 텀 두기
     * 2.req = 일반 큐 확인
     *
     * consume target = retry == null ? req : retry
     * consume target.async consume
     *      : api call
     *      : if exception occur, put retry queue
     *      : if retry permit num exceeds, set fail to batch
     *
     * batchTaskChecker
     * : 폴링을 하면서 완료된 배치 잡과 실패한 배치 잡 체크, 알림 전송
     */


    /**
     * RedisStream에서 메시지를 읽어 큐에 추가합니다.
     */
    @Scheduled(fixedDelay = 1000)
    public void consume() {
        log.info("Consumer group {} consume started", CONSUMER_GROUP);
        StreamOperations<String, String, Object> stringStringObjectStreamOperations = redisTemplateWithObj.opsForStream(new Jackson2HashMapper(true));
        List<ObjectRecord<String, OdsayApiRequest>> records = stringStringObjectStreamOperations
                .read(
                        OdsayApiRequest.class,
                        Consumer.from(CONSUMER_GROUP, CONSUMER_NAME),
                        StreamReadOptions.empty()
                                .count(50)
                                .block(Duration.ofSeconds(3)),
                        StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()) // 해당 라인 필요함?
                );

        if (records != null && !records.isEmpty()) {
            log.info("Consumer group {} consumed {} records", CONSUMER_GROUP, records.size());
            records.stream()
                    .map(obj -> obj.getValue())
                    .forEach(System.out::println);

            queue.addAll(records);
        }
    }


    public void startWorker() {
        Thread thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    // 재시도 큐에 메시지가 존재하면 그걸 꺼내오면 됨
                    ObjectRecord<String, OdsayApiRequest> take = queue.take();
                    rateLimiter.executeSupplier(() ->
                            executor.submit(() -> {
                                // api 요청
                                OdsayApiRequest request = take.getValue();
                                TransitRoute transitRoute = transitRouteApiAdapter.getTransitRouteBetweenLocations(
                                        request.getOrigin(), request.getDestination());
                                // 응답 저장
                                // batchId = [응답 1, 응답 2, 응답 3, 응답 4]
                                // seq: obj를 저장한다음, 배치 작업이 완료되면 seq대로 정렬 후 obj를 꺼내는...
                                // batch_id_1 : {{seq: 0, content: class}}
                                // batch 메타 데이터 업데이트: 완료 처리
                                // ack 처리
                                redisTemplateWithObj.opsForStream().acknowledge(CONSUMER_GROUP, take);
                    }));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private RateLimiterRegistry initializeRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMillis(200))
                .limitForPeriod(1)
                .timeoutDuration(Duration.ofSeconds(20))
                .build();

        return RateLimiterRegistry.of(config);
    }

    public void consume2() {

    }

    @PreDestroy
    public void destroy() {
        log.info("Destroying TransitRouteItemConsumer");
        // pending list에 있는거 dlq로 보내기?
    }

}
