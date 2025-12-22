package com.example.mohago_nocar.plan.domain.service;

import com.example.mohago_nocar.plan.presentation.v1.PlanTravelCourseRequestDto;
import com.example.mohago_nocar.plan.application.v1.response.PlanTravelCourseResponseDto;

import java.util.concurrent.CompletableFuture;

public interface TravelCoursePlanUseCaseV1 {

    CompletableFuture<PlanTravelCourseResponseDto> planCourse(PlanTravelCourseRequestDto dto);

}
