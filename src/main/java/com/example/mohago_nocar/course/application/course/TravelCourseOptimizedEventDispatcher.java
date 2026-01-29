package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class TravelCourseOptimizedEventDispatcher {

    private final TravelCourseUseCase travelCourseUseCase;
    private final TravelCourseOptimizedEventConsumer consumer;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        dispatch();
    }

    public void dispatch() {
        executorService.submit(() -> {
            int pollSizeAtOnce = 10;
            List<EventProcessStatus> unConsumedStatus = List.of(EventProcessStatus.CREATED, EventProcessStatus.PENDING_RETRY);

            while (true) {
                // poll unconsumed events
                List<CourseOptimizedEvent> unConsumedEvents =
                        travelCourseUseCase.getOldestOptimizedCourseEvents(pollSizeAtOnce, unConsumedStatus);
                if (unConsumedEvents.isEmpty()) {
                    int pauseTimeInSec = 3;
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(pauseTimeInSec));
                    continue;
                }

                // dispatch the event to consumer
                for (CourseOptimizedEvent event : unConsumedEvents) {
                    try {
                        consumer.consume(event);
                    } catch (Exception ex) {
                        travelCourseUseCase.completeOptimizedEventConsumeWithFailure(event, ex);
                    }
                }
            }
        });
    }

}
