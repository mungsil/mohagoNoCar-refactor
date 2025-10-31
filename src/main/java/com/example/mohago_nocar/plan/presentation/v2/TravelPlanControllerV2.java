package com.example.mohago_nocar.plan.presentation.v2;

import com.example.mohago_nocar.global.common.response.ApiResponse;
import com.example.mohago_nocar.plan.application.v2.PlanTravelCourseResponseDtoV2;
import com.example.mohago_nocar.plan.domain.service.TravelCourseUseCaseV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/travel-plan")
@Tag(name = "Plan", description = "여행 코스 설계")
public class TravelPlanControllerV2 {

    private final TravelCourseUseCaseV1 travelPlanUseCase;

    @PostMapping
    public ApiResponse<PlanTravelCourseResponseDtoV2> planTravelCourse(
            @RequestBody @Valid PlanTravelCourseRequestDtoV2 request
    ) {
        // batch_id 생성
        // 유저 아이디와 batch_id를 매핑해서 저장
        // 유저 아이디와 fcm 토큰 저장
        // 논블로킹으로 여행 api 설계 요청
        // batch_id 반환
    }

}
