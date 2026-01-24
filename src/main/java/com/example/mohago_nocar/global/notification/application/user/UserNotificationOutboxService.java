package com.example.mohago_nocar.global.notification.application.user;

import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.global.notification.NotificationMessagingException;
import com.example.mohago_nocar.global.notification.domain.UserNotificationMessageOutbox;
import com.example.mohago_nocar.global.notification.infrastructure.UserNotificationMessageOutboxJpaRepository;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import com.example.mohago_nocar.user.domain.UserUseCase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserNotificationOutboxService {

    private final UserNotificationMessageOutboxJpaRepository outboxRepository;
    private final UserUseCase userUseCase;
    private final FirebaseMessaging firebaseMessaging;

    @Transactional
    public void save(UserNotificationDto dto) {
        UserNotificationMessageOutbox msg = UserNotificationMessageOutbox.from(dto);
        outboxRepository.save(msg);
    }

    @Transactional
    public List<UserNotificationMessageOutbox> findUnpublished(int size) {
        return outboxRepository.findByStatusInOrderByCreatedDateAsc(
                List.of(EventProcessStatus.PENDING), size);
    }

    public void publish(UserNotificationMessageOutbox messageOutbox) {
        UUID userId = messageOutbox.getUserId();
        AnonymousUser user = userUseCase.findByIdOrThrow(userId);
        Message message = convertToFcmMessage(messageOutbox, user);
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new NotificationMessagingException("FCM 메시지 전송 중 오류가 발생했습니다", e);
        }
    }

    private Message convertToFcmMessage(UserNotificationMessageOutbox target, AnonymousUser user) {
        Notification notification = Notification.builder()
                .setTitle(target.getTitle())
                .setBody(target.getBody())
                .build();

        Message.Builder builder = Message.builder()
                .setToken(user.getFcmToken())
                .setNotification(notification);

        for (Map.Entry<String, String> entry : target.getCustomData().entrySet()) {
            builder.putData(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    @Transactional
    public void processFailure(UserNotificationMessageOutbox messageOutbox, Throwable throwable) {
        log.error("메시지 전송 시도 중 오류가 발생했습니다. ", throwable);
        if (throwable instanceof NotificationMessagingException) {
            if (messageOutbox.isFinalRetry()) {
                messageOutbox.markFailWithReason(throwable);
                log.warn("최대 재시도 횟수에 도달했습니다. outbox id: {}", messageOutbox.getId());
            } else {
                messageOutbox.incrementRetryCount();
            }
        } else {
            messageOutbox.markFailWithReason(throwable);
        }
        outboxRepository.save(messageOutbox);
    }

    @Transactional
    public void markAsPublished(UserNotificationMessageOutbox messageOutbox) {
        messageOutbox.markAsPublished();
        outboxRepository.save(messageOutbox);
    }

}
