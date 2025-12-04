package com.example.mohago_nocar.transit.infrastructure.queue.consumer;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

// transit route req outer Consumer
// bridge consumer,

// inner,
// TransitRouteRequestForwarder -> TransitRouteReqForwarder
// TransitRouteRequestProcessor -> PrioritizedTransitRouteProcessor
@Component
@RequiredArgsConstructor
@Slf4j
public class TransitRouteRequestForwarder implements StreamListener<String, ObjectRecord<String, String>> {

    @Value("${redis.streams.odsay.main}")
    private String streamKey;

    private final String consumerGroup = "processors";
    private final String consumer = "processor-1";

    private StreamMessageListenerContainer<String, ObjectRecord<String, String>> listenerContainer;
    private Subscription subscription;
    private RateLimiter rateLimiter;

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final TransitRouteRequestProcessor transitRouteRequestProcessor;

    @PostConstruct
    public void init() {
        rateLimiter = configureRateLimiter().rateLimiter("transit-api-producer");

        // Consumer Group 설정
        createStreamConsumerGroupIfNotExists(streamKey, consumerGroup);

        // StreamMessageListenerContainer 설정
        this.listenerContainer = StreamMessageListenerContainer.create(
                stringRedisTemplate.getConnectionFactory(),
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .targetType(String.class)
                        .pollTimeout(Duration.ofSeconds(3))
//                        .errorHandler()
                        .build()
        );

        // Subscription 설정
        this.subscription = this.listenerContainer.receive(
                Consumer.from(this.consumerGroup, consumer),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                this
        );

        // Redis listen 시작
        this.listenerContainer.start();
    }

    private void createStreamConsumerGroupIfNotExists(String streamKey, String consumerGroup) {
        try {
            stringRedisTemplate.opsForStream()
                    .createGroup(streamKey, ReadOffset.from("0"), consumerGroup);
            log.info("Consumer group {} created", consumerGroup);
        } catch (Exception e) {
            log.error("Exception occurs during creating consumer group : {}", e.getMessage());
        }
    }

    @Override
    public void onMessage(ObjectRecord<String, String> message) {

        rateLimiter.executeRunnable(() -> {
            transitRouteRequestProcessor.receive(message);
            transitRouteRequestProcessor.onAfterSuccess(() -> ackAndDeleteEntry(message));
            transitRouteRequestProcessor.onAfterFailure(() -> ackAndDeleteEntry(message));
        });

    }

    private void ackAndDeleteEntry(ObjectRecord<String, String> message) {
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
                message.getId().getValue()       // ARGV[3]
        );

        Long acked = executed.get(0);
        Long deleted = executed.get(1);

        if (acked == 0 || deleted == 0) {
            log.warn("[Redis] ackAndDeleteEntry: ack={}, del={}, id={}", acked, deleted, message.getId());
        }

    }

    @PreDestroy
    public void stopListenerContainer() {
        if (listenerContainer != null) {
            listenerContainer.stop();
            log.info("Listener container stopped");
        }
    }

    private RateLimiterRegistry configureRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(5)
                .timeoutDuration(Duration.ofMinutes(5))
                .build();

        return RateLimiterRegistry.of(config);
    }

}


