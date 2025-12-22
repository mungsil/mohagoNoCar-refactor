package com.example.mohago_nocar.plan.application.v1.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PlanTravelCourseResponseDto(
        List<TravelRouteResponseDto> travelRoutes
) {

    public static PlanTravelCourseResponseDto of(List<TravelRouteResponseDto> travelRoutes) {
        return PlanTravelCourseResponseDto.builder()
                .travelRoutes(travelRoutes)
                .build();
    }

}
