package com.example.mohago_nocar.global.notification.infrastructure.fcm;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Builder
@RequiredArgsConstructor
@Getter
public class FcmMessage {

    private final String fcmToken;
    private final Map<String, String> customData;
    private final Notification notification;

    public static FcmMessage create(String title, String body, String fcmToken, Map<String, String> customData) {
        return FcmMessage.builder()
                .fcmToken(fcmToken)
                .customData(customData)
                .notification(Notification.builder().setTitle(title).setBody(body).build())
                .build();
    }

    public Message toMessage(FcmMessage fcmMessage) {
        Message.Builder builder = Message.builder()
                .setToken(fcmMessage.getFcmToken())
                .setNotification(fcmMessage.getNotification());

        for (Map.Entry<String, String> entry : fcmMessage.getCustomData().entrySet()) {
            builder.putData(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

}
