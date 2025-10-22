package com.example.mohago_nocar.plan.domain.service;

import com.example.mohago_nocar.plan.presentation.request.PlanTravelCourseRequestDto;
import com.example.mohago_nocar.plan.application.response.PlanTravelCourseResponseDto;

public interface TravelPlanUseCase {

    PlanTravelCourseResponseDto planCourse(PlanTravelCourseRequestDto dto);

}
