package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TravelCourseEventOutboxHandler {

    private final TravelCourseEventService outboxService;

    @Scheduled(fixedDelay = 1000)
    public void handle() {
        long handleStartTime = System.nanoTime();

        List<TravelCourseOptimizedEvent> unpublishedList = outboxService.findUnpublished(10);
        for (TravelCourseOptimizedEvent unpublished : unpublishedList) {
            try {
                outboxService.publish(unpublished);
                outboxService.markAsPublished(unpublished);
            } catch (Exception e) {
                outboxService.processFailure(unpublished, e);
            }
        }

        long handleEndTime = System.nanoTime();
        log.info("Handle completed in {} ms", TimeUnit.NANOSECONDS.toMillis(handleEndTime - handleStartTime));
    }

}
