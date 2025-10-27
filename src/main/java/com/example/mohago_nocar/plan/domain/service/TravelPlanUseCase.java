package com.example.mohago_nocar.plan.domain.service;

import com.example.mohago_nocar.plan.presentation.request.PlanTravelCourseRequestDto;
import com.example.mohago_nocar.plan.application.response.PlanTravelCourseResponseDto;

import java.util.concurrent.CompletableFuture;

public interface TravelPlanUseCase {

    CompletableFuture<PlanTravelCourseResponseDto> planCourse(PlanTravelCourseRequestDto dto);

}
