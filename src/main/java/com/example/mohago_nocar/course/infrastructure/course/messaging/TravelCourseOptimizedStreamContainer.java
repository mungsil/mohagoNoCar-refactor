package com.example.mohago_nocar.course.infrastructure.course.messaging;

import io.lettuce.core.RedisBusyException;
import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class TravelCourseOptimizedStreamContainer implements SmartLifecycle {

    @Value("${redis.streams.course.optimized.main}")
    private String streamKey;

    private final String consumerGroupName;
    private final String consumerName;
    public final int messagesPerPolling;

    private final TravelCourseOptimizedMessageConsumer messageConsumer;
    private final TravelCourseOptimizedStreamRecoveryManager recoveryManager;
    private final StringRedisTemplate stringRedisTemplate;

    private StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamMessageListenerContainer;

    @PostConstruct
    public void init() {
        createStreamConsumerGroupIfNotExists(streamKey, consumerGroupName);
        this.streamMessageListenerContainer = createContainer();
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
                    "Unexpected error while creating consumer group: ", cause);
        }
    }

    private StreamMessageListenerContainer<String, ObjectRecord<String, String>> createContainer() {
        StreamMessageListenerContainer<String, ObjectRecord<String, String>> listenerContainer =
                StreamMessageListenerContainer.create(
                        Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()),
                        StreamMessageListenerContainer
                                .StreamMessageListenerContainerOptions.builder()
                                .targetType(String.class)
                                .pollTimeout(Duration.ofSeconds(3))
                                .batchSize(messagesPerPolling)
                                .errorHandler(throwable -> {
                                    log.error("Error occurs during Redis stream message consuming", throwable);
                                    Sentry.captureException(throwable);
                                })
                                .build()
                );

        listenerContainer.register(
                StreamMessageListenerContainer.StreamReadRequest
                        .builder(StreamOffset.create(streamKey, ReadOffset.lastConsumed()))
                        //turn off auto shutdown of stream consumer if an error occurs.
                        .cancelOnError(throwable -> false)
                        .consumer(Consumer.from(
                                messageConsumer.getConsumerGroupName(),
                                messageConsumer.getConsumerName()))
                        .autoAcknowledge(false).build()
                , messageConsumer);

        return listenerContainer;
    }

    @Override
    public void start() {
//        recoveryManager.recovery(streamKey, consumerGroupName, consumerName);
        streamMessageListenerContainer.start();
    }

    @Override
    public void stop() {
        streamMessageListenerContainer.stop();
    }

    @Override
    public boolean isRunning() {
        return streamMessageListenerContainer.isRunning();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

}




