package com.example.mohago_nocar.plan.domain.service;

import com.example.mohago_nocar.plan.presentation.request.PlanTravelCourseRequestDto;
import com.example.mohago_nocar.plan.presentation.response.PlanResponseDto;
import reactor.core.publisher.Mono;

public interface TravelPlanUseCase {

    Mono<PlanResponseDto> planCourse(PlanTravelCourseRequestDto dto);

}
