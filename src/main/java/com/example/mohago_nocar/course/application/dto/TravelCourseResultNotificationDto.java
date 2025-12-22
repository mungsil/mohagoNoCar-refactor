package com.example.mohago_nocar.course.application.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TravelCourseResultNotificationDto(
        Long travelCourseId,
        UUID anonymousUserId
) {

    public static TravelCourseResultNotificationDto of(
            Long travelCourseId,
            UUID anonymousUserId
    ) {
        return new TravelCourseResultNotificationDto(travelCourseId, anonymousUserId);
    }

}
