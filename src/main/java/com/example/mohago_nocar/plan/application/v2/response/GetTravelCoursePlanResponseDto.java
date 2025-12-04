package com.example.mohago_nocar.plan.application.v2.response;

import com.example.mohago_nocar.plan.application.v1.response.TravelRouteResponseDto;
import lombok.Builder;

import java.util.List;

@Builder
public record GetTravelCoursePlanResponseDto(
        List<TravelRouteResponseDto> travelRoutes
) {

    public static GetTravelCoursePlanResponseDto of(List<TravelRouteResponseDto> travelRoutes) {
        return GetTravelCoursePlanResponseDto.builder()
                .travelRoutes(travelRoutes)
                .build();
    }

}
