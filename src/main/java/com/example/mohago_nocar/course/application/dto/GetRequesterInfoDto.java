package com.example.mohago_nocar.course.application.dto;

import java.util.UUID;

// todo 삭제
public record GetRequesterInfoDto(
        UUID anonymousUserId,
        String fcmToken
) {
}
