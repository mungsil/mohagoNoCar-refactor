package com.example.mohago_nocar.plan.domain.service;

import com.example.mohago_nocar.plan.application.v2.PlanTravelCourseResponseDtoV2;
import com.example.mohago_nocar.plan.presentation.v2.PlanTravelCourseRequestDtoV2;

public interface TravelCourseUseCaseV2 {

    public TravelPlanResponseDtoV2 plan(PlanTravelCourseRequestDtoV2 request);

}
