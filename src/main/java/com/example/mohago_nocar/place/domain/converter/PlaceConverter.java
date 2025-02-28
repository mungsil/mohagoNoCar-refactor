package com.example.mohago_nocar.place.domain.converter;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.place.domain.model.*;
import com.example.mohago_nocar.place.infrastructure.externalApi.kakao.dto.response.KakaoPlacesResponse;
import com.example.mohago_nocar.place.infrastructure.externalApi.kakao.dto.response.KakaoPlacesResponse.KakaoPlaceResponse;
import com.example.mohago_nocar.place.presentation.response.NearPlaceResponseDto;

import java.util.List;

public class PlaceConverter {

    public static List<Place> convertToPlaces(KakaoPlacesResponse places) {
        return places.documents().stream()
                .map(PlaceConverter::convertToPlace)
                .toList();
    }

    public static Place convertToPlace(KakaoPlaceResponse dto) {
        return Place.from(
                dto.id(),
                dto.place_name(),
                Coordinate.from(dto.x(), dto.y()),
                dto.address_name(),
                dto.place_url(),
                PlaceCategory.getCategoryByCode(dto.category_group_code())
        );
    }

    public static List<NearPlaceResponseDto> convertToNearPlaceResponseDtos(Long festivalId, List<Place> places) {
        return places.stream()
                .map(place -> NearPlaceResponseDto.of(festivalId, place))
                .toList();
    }

}
