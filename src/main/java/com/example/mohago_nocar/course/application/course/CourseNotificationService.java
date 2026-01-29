package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseCompletionMessage;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.course.domain.model.course.CourseNotificationOutbox;
import com.example.mohago_nocar.course.infrastructure.course.CourseNotificationOutboxRepository;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseNotificationService {

    private final FirebaseMessaging messaging;
    private final TravelCourseUseCase travelCourseUseCase;
    private final CourseNotificationOutboxRepository notificationOutboxRepository;

    public List<CourseNotificationOutbox> getOldestPendingNotificationOutBoxes(int size, int maxTryNum) {
        Assert.isTrue(size > 0, "Size must be greater than 0");

        return notificationOutboxRepository.findOldestPendingByLessThanOrEqualTryCount(size, maxTryNum);
    }

    @Transactional
    public void send(CourseNotificationOutbox outbox) {
        AnonymousUser user = travelCourseUseCase.getRequestUserOrThrow(outbox.getTravelCourseId());
        CourseOptimizedEvent event = travelCourseUseCase.getOptimizedEventOrThrow(outbox.getTravelCourseId());

        try {
            messaging.send(buildFcmMsg(user, event));
            outbox.markSuccess();
            notificationOutboxRepository.save(outbox);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send notification", e);
            throw new RuntimeException(e);
        }
    }

    private Message buildFcmMsg(AnonymousUser user, CourseOptimizedEvent event) {
        TravelCourseCompletionMessage notificationMsg = event.getCompletionNotificationMsg();

        Notification notification = Notification.builder()
                .setTitle(notificationMsg.getTitle())
                .setBody(notificationMsg.getBody())
                .build();

        Message.Builder builder = Message.builder()
                .setToken(user.getFcmToken())
                .setNotification(notification);

        builder.putData("travelCourseId", String.valueOf(event.getTravelCourseId()));

        return builder.build();
    }

    @Transactional
    public int incrementTryCount(CourseNotificationOutbox outbox, int incrementTryCount) {
        outbox.incrementTryCount(incrementTryCount);
        notificationOutboxRepository.save(outbox);
        return outbox.getTryCount();
    }
}
