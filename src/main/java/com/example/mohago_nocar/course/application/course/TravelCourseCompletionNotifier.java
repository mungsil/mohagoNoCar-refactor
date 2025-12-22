package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseCompletionMessage;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.global.notification.application.user.UserNotificationDto;
import com.example.mohago_nocar.global.notification.application.user.UserNotificationService;
import com.example.mohago_nocar.global.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class TravelCourseCompletionNotifier {

    private final UserNotificationService userNotificationService;
    private final TravelCourseUseCase travelCourseUseCase;

    /**
     * 알림을 전송합니다. 만일 알림 전송 이력이 있다면 알림을 전송하지 않습니다.
     * @param travelCourseId 알림을 전송할 코스 아이디
     * @param result 코스 처리 결과
     * @param exceptionHandler 알림 전송 중 예외 발생 시 사용되는 핸들러
     */
    public void sendNotificationOnce(
            Long travelCourseId,
            Result result,
            TravelCourseNotifyExceptionHandler exceptionHandler
    ) {
        try {
            TravelCourse course = travelCourseUseCase.findById(travelCourseId).orElseThrow();
            if (course.getNotificationSent()) {
                return;
            }

            TravelCourseCompletionMessage message = result.isSuccess() ?
                    TravelCourseCompletionMessage.SUCCESS : TravelCourseCompletionMessage.FAILURE;

            userNotificationService.send(new UserNotificationDto(
                    message.getTitle(),
                    message.getBody(),
                    course.getAnonymousUserId(),
                    Map.of("travelCourseId", String.valueOf(course.getId()))
            ));

            travelCourseUseCase.markNotificationSent(travelCourseId);
        } catch (Exception e) {
            log.info("알림 전송에 실패했습니다. ", e);
            exceptionHandler.handle(e);
        }
    }

    @FunctionalInterface
    public interface TravelCourseNotifyExceptionHandler {

        void handle(Exception e);

    }

}
