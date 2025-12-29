package com.example.mohago_nocar.course.infrastructure.course.messaging;

import com.example.mohago_nocar.course.application.course.TravelCourseEventHandler;
import com.example.mohago_nocar.course.domain.event.ThrottlingCompletedEvent;
import com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.global.messaging.DeadLetterQueueEntryDto;
import com.example.mohago_nocar.global.messaging.DeadLetterQueueService;
import com.example.mohago_nocar.global.util.RedisStreamHelper;
import com.example.mohago_nocar.global.common.RetryPolicy;
import com.example.mohago_nocar.global.util.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;

import java.util.concurrent.*;

@Slf4j
@Getter
@RequiredArgsConstructor
public class TravelCourseOptimizedMessageConsumer
        implements StreamListener<String, ObjectRecord<String, String>> {

    private final String streamName;
    private final String consumerGroupName;
    private final String consumerName;

    private final RedisStreamHelper redisStreamHelper;
    private final DeadLetterQueueService dlqService;
    private final ObjectMapper objectMapper;
    private final RetryPolicy retryPolicy;
    private final TravelCourseEventHandler eventHandler;
    private final TravelCourseUseCase travelCourseUseCase;

    private ExecutorService executorService;
    private Semaphore semaphore;

    @PostConstruct
    public void init() {
        executorService = Executors.newVirtualThreadPerTaskExecutor();
        semaphore = new Semaphore(0);
    }

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        log.info("Received message {}", message);

        executorService.submit(() -> {
            processEvent(message);
        });

        try {
            if (!semaphore.tryAcquire(1, TimeUnit.SECONDS)) {
                log.info("idle 대기 시간을 넘겼습니다. 다음 메시지를 처리할 수 있도록 컨슈머 스레드의 블로킹을 해제합니다.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void processEvent(ObjectRecord<String, String> message) {
        TravelCourseOptimizedEvent event = null;
        try {
            event = objectMapper.readValue(message.getValue(), TravelCourseOptimizedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("메시지 역직렬화 중 에러가 발생했습니다.");
            saveToDeadLetterQueue(message, e);
            ackAndDel(message);
            return;
        }

        try {
            eventHandler.handleEvent(event);
        } catch (Exception ex) {
            handleException(event, message, ex);
        } finally {
            ackAndDel(message);
        }
    }

    private void handleException(TravelCourseOptimizedEvent event, ObjectRecord<String, String> message, Exception e) {
        log.error(e.getMessage(), e);
        if (retryPolicy.isRetryable(e)) {
            saveToDeadLetterQueue(message, e);
            return;
        }

        eventHandler.processFailEvent(event);
    }

    private void saveToDeadLetterQueue(ObjectRecord<String, String> message, Exception ex) {
        log.info("재시도 가능한 예외입니다. 재처리를 위해 메시지를 저장합니다.");
        DeadLetterQueueEntryDto dto = DeadLetterQueueEntryDto.of(
                message.getId().getValue(), streamName, getConsumerGroupName(), getConsumerName(), message.getValue(), ex);
        dlqService.save(dto);
    }

    private void ackAndDel(ObjectRecord<String, String> message) {
        redisStreamHelper.acknowledgeAndDelete(streamName, consumerGroupName, message.getId());
    }

    @EventListener
    public void allowNextConsume(ThrottlingCompletedEvent event) {
        log.info("travelCourse ID:{} throttling completed", event.getTravelCourseId());
        semaphore.release();
    }

    @PreDestroy
    private void destroy() {
        log.info("ExecutorService in TravelCourseOptimizedMessageConsumer shutdown 시작");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                log.warn("정상 종료에 실패했습니다. 강제 종료를 시작합니다.");
                executorService.shutdownNow();
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.error("강제 종료에 실패하였습니다.");
                }
            }
        } catch (InterruptedException e) {
            log.error("인터럽트 발생", e);
            executorService.shutdownNow();
        }
    }

}
