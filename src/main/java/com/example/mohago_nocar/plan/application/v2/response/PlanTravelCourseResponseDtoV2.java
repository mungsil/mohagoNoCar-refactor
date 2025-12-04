package com.example.mohago_nocar.plan.application.v2.response;

import lombok.Builder;

@Builder
public record PlanTravelCourseResponseDtoV2(
        String userId,
        Long planId
) {
}
