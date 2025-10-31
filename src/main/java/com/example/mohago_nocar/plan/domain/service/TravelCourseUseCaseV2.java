package com.example.mohago_nocar.plan.domain.service;

import com.example.mohago_nocar.plan.presentation.TravelPlanResponseDtoV2;
import com.example.mohago_nocar.plan.presentation.request.PlanTravelCourseRequestDtoV2;

public interface TravelCourseUseCaseV2 {

    public TravelPlanResponseDtoV2 plan(PlanTravelCourseRequestDtoV2 request);

}
