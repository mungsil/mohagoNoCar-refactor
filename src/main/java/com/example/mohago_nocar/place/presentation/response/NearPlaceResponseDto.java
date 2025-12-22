package com.example.mohago_nocar.place.presentation;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.place.domain.model.Place;

import lombok.Builder;

@Builder
public record NearPlaceResponseDto(
        String id,
        String name,
        Long festivalId,
        Coordinate coordinate,
        String address,
        String placeUrl,
        String category
) {
    public static NearPlaceResponseDto of(Long festivalId, Place place) {
        return new NearPlaceResponseDtoBuilder()
                .id(place.getKakaoId())
                .name(place.getName())
                .festivalId(festivalId)
                .coordinate(place.getCoordinate())
                .address(place.getAddress())
                .placeUrl(place.getPlaceUrl())
                .category(place.getCategory().name())
                .build();
    }
}
