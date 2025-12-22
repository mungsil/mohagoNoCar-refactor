package com.example.mohago_nocar.course.presentation.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record GetOptimizedTravelCourseRequestDto(
        @NotBlank
        UUID anonymousUserId,
        @NotBlank
        Long travelCourseId
) {
}
