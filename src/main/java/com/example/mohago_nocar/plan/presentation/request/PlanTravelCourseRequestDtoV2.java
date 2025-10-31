package com.example.mohago_nocar.plan.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PlanTravelCourseRequestDtoV2(

        @Schema(description = "클라이언트가 발급한 uuid로, 회원 아이디 대신 사용합니다.")
        String userId,

        @Schema(description = "fcm token")
        String fcmToken,

        @Schema(description = "선택된 여행 장소들의 아이디", example = "[1234]")
        List<String> placeIds
) {
}
