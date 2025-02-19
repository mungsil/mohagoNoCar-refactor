package com.example.mohago_nocar.festival.presentation.response;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import lombok.Builder;

@Builder
public record FestivalLocationResponseDto(
        Coordinate coordinate
) {
    public static FestivalLocationResponseDto of(Coordinate coordinate) {
        return new FestivalLocationResponseDtoBuilder()
                .coordinate(coordinate)
                .build();
    }
}
