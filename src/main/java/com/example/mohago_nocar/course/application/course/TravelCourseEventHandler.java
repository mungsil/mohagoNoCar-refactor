package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.application.route.RouteStepService;
import com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseCompletionMessage;
import com.example.mohago_nocar.course.domain.model.course.ProcessedCourse;
import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import com.example.mohago_nocar.course.domain.repository.ProcessedCourseRepository;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.global.notification.application.user.UserNotificationDto;
import com.example.mohago_nocar.global.notification.application.user.UserNotificationOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class TravelCourseEventHandler {

    private final TravelCourseUseCase travelCourseUseCase;
    private final ProcessedCourseRepository processedCourseRepository;
    private final UserNotificationOutboxService notificationOutboxService;
    private final RouteStepService routeStepService;
    private final TransactionTemplate transactionTemplate;

    public void handleEvent(TravelCourseOptimizedEvent event) {
        List<RouteStep> routeSteps = travelCourseUseCase.fetchTravelCourseRoutes(event.getTravelCourseId());

        transactionTemplate.executeWithoutResult(tx -> {
            if (isProcessed(event.getTravelCourseId(), true)) {
                log.info("이미 처리 중이거나 완료된 코스입니다. 대상: {}", event);
                return;
            }
            routeStepService.saveAll(routeSteps);
            UserNotificationDto notificationDto = createNotificationMessage(event, true);
            notificationOutboxService.save(notificationDto);
        });
    }

    @Transactional
    public void processFailEvent(TravelCourseOptimizedEvent event) {
        if (isProcessed(event.getTravelCourseId(), false)) {
            log.info("이미 처리 중이거나 완료된 코스입니다. 대상: {}", event);
            return;
        }
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

    public boolean isProcessed(Long travelCourseId, boolean isSuccess) {
        ProcessedCourse history = ProcessedCourse.of(travelCourseId, isSuccess);

        try {
            processedCourseRepository.save(history);
        } catch (DataIntegrityViolationException e) {
            return true;
        }

        return false;
    }

}
