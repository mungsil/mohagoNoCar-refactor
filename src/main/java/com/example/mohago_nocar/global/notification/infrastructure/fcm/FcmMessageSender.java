package com.example.mohago_nocar.global.notification.infrastructure.fcm;

import com.example.mohago_nocar.global.notification.NotificationMessagingException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class FcmMessageSender {

    private final FirebaseMessaging firebaseMessaging;

    public String send(FcmMessage fcmMessage) {
        Message message = fcmMessage.toMessage(fcmMessage);
        return sendMessage(message);
    }

    private String sendMessage(Message message) {
        try {
            return firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 알림 전송에 실패했습니다. 에러 메시지={}", e.getMessage());
            throw new NotificationMessagingException("알림 전송에 실패했습니다.", e);
        }
    }

}
