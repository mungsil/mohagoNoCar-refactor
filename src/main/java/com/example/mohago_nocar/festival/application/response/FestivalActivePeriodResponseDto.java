package com.example.mohago_nocar.festival.application.response;

import com.example.mohago_nocar.festival.domain.model.vo.ActivePeriod;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record FestivalActivePeriodResponseDto(
        LocalDate startDate,
        LocalDate endDate
) {

    public static FestivalActivePeriodResponseDto of(ActivePeriod activePeriod) {
        return new FestivalActivePeriodResponseDtoBuilder()
                .startDate(activePeriod.getStartDate())
                .endDate(activePeriod.getEndDate())
                .build();
    }
}
