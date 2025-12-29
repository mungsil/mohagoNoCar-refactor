package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseCompletionMessage;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEventHandleHistory;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseStatus;
import com.example.mohago_nocar.course.domain.repository.TravelCourseOptimizedEventHandleHistoryRepository;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.global.notification.application.user.UserNotificationDto;
import com.example.mohago_nocar.global.notification.application.user.UserNotificationOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class TravelCourseEventHandler {

    private final TravelCourseUseCase travelCourseUseCase;
    private final TravelCourseOptimizedEventHandleHistoryRepository handleHistoryRepository;
    private final UserNotificationOutboxService notificationOutboxService;

    @Transactional
    public void handleEvent(TravelCourseOptimizedEvent event) {
        travelCourseUseCase.generateTransitRoute(event.getTravelCourseId());
        processSuccessEvent(event);
    }

    private void processSuccessEvent(TravelCourseOptimizedEvent event) {
        travelCourseUseCase.updateUncompletedCourseStatus(event.getTravelCourseId(), TravelCourseStatus.SUCCEEDED);
        UserNotificationDto notificationDto = createNotificationMessage(event, true);
        notificationOutboxService.save(notificationDto);
    }

    @Transactional
    public void processFailEvent(TravelCourseOptimizedEvent event) {
        travelCourseUseCase.updateUncompletedCourseStatus(event.getTravelCourseId(), TravelCourseStatus.FAILED);
        UserNotificationDto notificationDto = createNotificationMessage(event, false);
        notificationOutboxService.save(notificationDto);
    }

    private UserNotificationDto createNotificationMessage(TravelCourseOptimizedEvent event, boolean isSuccess) {
        TravelCourseCompletionMessage message = isSuccess ?
                TravelCourseCompletionMessage.SUCCESS : TravelCourseCompletionMessage.FAILURE;

        return new UserNotificationDto(
                message.getTitle(),
                message.getBody(),
                event.getAnonymousUserId(),
                Map.of("travelCourseId", String.valueOf(event.getTravelCourseId()))
        );
    }

    public boolean hasHandleHistory(Long travelCourseId) {
        TravelCourseOptimizedEventHandleHistory history = TravelCourseOptimizedEventHandleHistory.of(travelCourseId);

        try {
            handleHistoryRepository.save(history);
        } catch (DataIntegrityViolationException e) {
            return true;
        }

        return false;
    }

}
