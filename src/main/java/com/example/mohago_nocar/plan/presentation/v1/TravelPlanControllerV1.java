package com.example.mohago_nocar.plan.presentation.v1;

import com.example.mohago_nocar.global.common.response.ApiResponse;
import com.example.mohago_nocar.plan.domain.service.TravelCourseUseCaseV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/travel-plan")
@Tag(name = "Plan", description = "여행 코스 설계")
public class TravelPlanControllerV1 {

    private final TravelCourseUseCaseV1 travelCourseUseCaseV1;

    @PostMapping
    public CompletableFuture<ApiResponse<?>> planTravelCourse(
            @RequestBody @Valid PlanTravelCourseRequestDto requestDto
    ) {
        return travelCourseUseCaseV1.planCourse(requestDto)
                .thenApply(ApiResponse::ok);
    }

}
