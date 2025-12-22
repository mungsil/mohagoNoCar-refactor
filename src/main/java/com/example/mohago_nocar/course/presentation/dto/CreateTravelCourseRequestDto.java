package com.example.mohago_nocar.course.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

// todo 검증 추가
public record CreateTravelCourseRequestDto(

        @Schema(description = "FCM 토큰")
        String fcmToken,

        @Schema(description = "축제 아이디")
        Long festivalId,

        // todo 검증 어노테이션 추가
        @Size(max = 4)
        @Schema(description = "선택된 여행 장소들의 아이디로, 최소 2개-최대 4개 선택 가능", example = "[1234]")
        List<String> placeIds,

        @Schema(description = "여행 시작 날짜")
        LocalDate travelStartDate
) {
}
