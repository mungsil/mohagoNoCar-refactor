package com.example.mohago_nocar.plan.presentation;

import com.example.mohago_nocar.global.common.response.ApiResponse;
import com.example.mohago_nocar.plan.domain.service.TravelPlanUseCase;
import com.example.mohago_nocar.plan.presentation.request.PlanTravelCourseRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/travel-plan")
@Tag(name = "Plan", description = "여행 코스 설계")
public class TravelPlanController {

    private final TravelPlanUseCase travelPlanUseCase;

    @PostMapping
    public Mono<ApiResponse<?>> planTravelCourse(
            @RequestBody @Valid PlanTravelCourseRequestDto requestDto
    ) {
        return travelPlanUseCase.planCourse(requestDto)
                .map(ApiResponse::ok);
    }

}
