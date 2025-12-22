package com.example.mohago_nocar.course.presentation.dto;

import java.util.UUID;

public record CreateOptimizedTravelCourseAcceptedResponseDto(
        Long courseId,
        UUID anonymousUserId
) {

    public static CreateOptimizedTravelCourseAcceptedResponseDto of(Long courseId, UUID anonymousUserId){
        return new CreateOptimizedTravelCourseAcceptedResponseDto(courseId, anonymousUserId);
    }

}
