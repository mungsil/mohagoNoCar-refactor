package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.model.course.CourseStatus;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.course.infrastructure.course.messaging.TravelCourseOptimizedEventPublisher;
import com.example.mohago_nocar.global.common.exception.CustomException;
import com.example.mohago_nocar.global.common.exception.GlobalStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class TravelCourseOptimizedEventHandler {

    private final TravelCourseOptimizedEventPublisher eventPublisher;
    private final TravelCourseUseCase travelCourseUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(TravelCourseOptimizedEvent event) {
        try {
            eventPublisher.publish(event);
        } catch (Exception e) {
            log.error("이벤트 발송에 실패했습니다. 이벤트 = {}", event);
            throw new CustomException(GlobalStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void handleEvent(TravelCourseOptimizedEvent event) {
        travelCourseUseCase.generateTransitRoute(event.getTravelCourseId());
        markAsSucceeded(event.getTravelCourseId());
    }

    public void markAsSucceeded(Long travelCourseId) {
        travelCourseUseCase.updateCourseStatus(travelCourseId, CourseStatus.SUCCEEDED);
    }

    public void markAsWaitingReprocessing(Long travelCourseId) {
        travelCourseUseCase.updateCourseStatus(travelCourseId, CourseStatus.WAITING_REPROCESSING);
    }

    public void markAsFailed(Long travelCourseId) {
        travelCourseUseCase.updateCourseStatus(travelCourseId, CourseStatus.FAILED);
    }

}
