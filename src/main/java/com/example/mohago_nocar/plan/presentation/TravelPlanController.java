package com.example.mohago_nocar.plan.presentation;

import com.example.mohago_nocar.global.common.response.ApiResponse;
import com.example.mohago_nocar.plan.domain.service.TravelPlanUseCase;
import com.example.mohago_nocar.plan.presentation.request.PlanTravelCourseRequestDto;
import com.example.mohago_nocar.plan.presentation.response.PlanTravelCourseResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/travel-plan")
@Tag(name = "Plan", description = "여행 코스 설계")
public class TravelPlanController {

    private final TravelPlanUseCase travelPlanUseCase;

    @PostMapping
    public ApiResponse<?> planTravelCourse(
            @RequestBody @Valid PlanTravelCourseRequestDto requestDto
    ) {
        PlanTravelCourseResponseDto responseDto = travelPlanUseCase.planCourse(requestDto);
        return ApiResponse.ok(responseDto);
    }
}
