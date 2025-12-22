package com.example.mohago_nocar.plan.presentation.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record PlanTravelCourseRequestDto(
        Long festivalId,
        LocalDate travelDate,

        @Schema(description = "출발 시간", example = "09:10")
        LocalTime leaveTime,

        @Schema(description = "도착 시간", example = "10:00")
        LocalTime arrivalTime,

        @Schema(description = "선택된 여행 장소들의 아이디", example = "[1234]")
        List<String> placeIds
) {
}
