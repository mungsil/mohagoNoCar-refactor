package com.example.mohago_nocar.plan.domain.service;

import com.example.mohago_nocar.plan.presentation.request.PlanTravelCourseRequestDto;
import com.example.mohago_nocar.plan.presentation.response.PlanTravelCourseResponseDto;

import java.util.List;

public interface TravelPlanUseCase {

    PlanTravelCourseResponseDto planCourse(PlanTravelCourseRequestDto dto);

}
