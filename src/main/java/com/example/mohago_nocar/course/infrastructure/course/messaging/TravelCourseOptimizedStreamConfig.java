package com.example.mohago_nocar.course.infrastructure.course.messaging;

import com.example.mohago_nocar.course.application.course.TravelCourseEventHandler;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.global.messaging.DeadLetterQueueService;
import com.example.mohago_nocar.global.util.RedisStreamHelper;
import com.example.mohago_nocar.global.common.RetryPolicy;
import com.example.mohago_nocar.global.notification.application.developer.DeveloperNotificationUseCase;
import com.example.mohago_nocar.global.util.ObjectMapperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TravelCourseOptimizedStreamConfig {

    @Value("${redis.streams.course.optimized.main}")
    private String streamKey;

    private static final String CONSUMER_GROUP = "processors";
    private static final String CONSUMER_1 = "processor-1";
    private int messagesPerPolling = 3;

    @Bean
    public TravelCourseOptimizedStreamContainer travelCourseOptimizedStreamContainer(
            TravelCourseOptimizedMessageConsumer consumer,
            TravelCourseOptimizedStreamRecoveryManager recoveryManager,
            StringRedisTemplate stringRedisTemplate
    ) {
        return new TravelCourseOptimizedStreamContainer(
                CONSUMER_GROUP,
                CONSUMER_1,
                messagesPerPolling,
                consumer,
                recoveryManager,
                stringRedisTemplate
        );
    }

    @Bean
    public TravelCourseOptimizedStreamRecoveryManager travelCourseOptimizedStreamRecoveryManager(
            StringRedisTemplate stringRedisTemplate,
            TravelCourseOptimizedMessageConsumer consumer
    ){
        return new TravelCourseOptimizedStreamRecoveryManager(
                stringRedisTemplate, consumer
        );
    }

    @Bean
    public TravelCourseOptimizedMessageProducer travelCourseOptimizedEventPublisher(
            StringRedisTemplate stringRedisTemplate,
            ObjectMapperUtil objectMapperUtil
    ) {
        return new TravelCourseOptimizedMessageProducer(
                streamKey, stringRedisTemplate, objectMapperUtil);
    }

    @Bean
    public TravelCourseOptimizedMessageConsumer travelCourseOptimizedMessageConsumer(
            RedisStreamHelper redisStreamHelper,
            ObjectMapper objectMapper,
            DeadLetterQueueService deadLetterQueueService,
            RetryPolicy travelSpotOptimizedStreamMsgRetryPolicy,
            TravelCourseEventHandler travelCourseEventHandler,
            TravelCourseUseCase travelCourseUseCase
            ) {
        return new TravelCourseOptimizedMessageConsumer(
                streamKey,
                CONSUMER_GROUP,
                CONSUMER_1,
                redisStreamHelper,
                deadLetterQueueService,
                objectMapper,
                travelSpotOptimizedStreamMsgRetryPolicy,
                travelCourseEventHandler,
                travelCourseUseCase
                );
    }

    @Bean
    public TravelCourseOptimizedMessageRetryPolicy travelSpotOptimizedStreamMsgRetryPolicy() {
        return new TravelCourseOptimizedMessageRetryPolicy();
    }

    @Bean
    public LongPendingMessageReader travelSpotOptimizedLongPendingMessageReader(RedisStreamHelper redisStreamHelper) {
        return new LongPendingMessageReader(
                streamKey, CONSUMER_GROUP, CONSUMER_1, 200, 30_000, redisStreamHelper);
    }

    @Bean
    public LongPendingMessageHandler travelSpotOptimizedLongPendingMessageHandler(
            LongPendingMessageReader travelSpotOptimizedLongPendingMessageReader,
            DeveloperNotificationUseCase developerNotificationUseCase
    ) {
        return new LongPendingMessageHandler(
                travelSpotOptimizedLongPendingMessageReader,
                developerNotificationUseCase);
    }

}
