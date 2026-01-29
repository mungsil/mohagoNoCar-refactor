package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.domain.model.course.CourseNotificationOutbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CourseNotificationOutboxProcessor {

    private final CourseNotificationService notificationService;

    @Scheduled(fixedDelay = 3000)
    public void process() {
        int pollSize = 10;
        List<CourseNotificationOutbox> notificationOutBoxes =
                notificationService.getOldestPendingNotificationOutBoxes(pollSize, 3);

        // todo 비동기 처리
        // todo 에러 핸들링 - 알림 전송 실패 시 실패 이유 기록
        for (CourseNotificationOutbox notification : notificationOutBoxes) {
            int tryCount = notificationService.incrementTryCount(notification, 1);
            log.info("Processing notification outbox: {}, try #{}", notification, tryCount);

            notificationService.send(notification);
        }
    }

}
