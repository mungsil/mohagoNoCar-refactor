package com.example.mohago_nocar.course.presentation.dto;

import com.example.mohago_nocar.course.application.dto.RouteStepDto;

import java.util.List;

public record GetOptimizedTravelCourseResponseDto(
        List<? extends RouteStepDto> routeSteps
) {
    public static GetOptimizedTravelCourseResponseDto of(List<? extends RouteStepDto> stepDtos) {
        return new GetOptimizedTravelCourseResponseDto(stepDtos);
    }
}
