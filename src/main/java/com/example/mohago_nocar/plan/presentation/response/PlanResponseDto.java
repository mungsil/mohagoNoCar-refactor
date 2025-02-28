package com.example.mohago_nocar.plan.presentation.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PlanResponseDto(
        List<TravelRouteResponseDto> travelRoutes
) {

    public static PlanResponseDto from(List<TravelRouteResponseDto> travelRoutes) {
        return PlanResponseDto.builder()
                .travelRoutes(travelRoutes)
                .build();
    }

}
