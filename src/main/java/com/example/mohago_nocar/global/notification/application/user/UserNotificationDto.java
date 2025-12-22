package com.example.mohago_nocar.global.notification.application.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class UserNotificationDto {

    private String title;
    private String body;
    private UUID userId;
    private Map<String, String> customData;

    public static UserNotificationDto of(String title, String body, UUID userId, Map<String, String> customData) {
        return new UserNotificationDto(title, body, userId, customData);
    }

}
