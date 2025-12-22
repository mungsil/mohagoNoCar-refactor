package com.example.mohago_nocar.place.domain.service;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.place.domain.model.Place;
import com.example.mohago_nocar.place.presentation.NearPlaceResponseDto;

import java.util.List;

public interface PlaceUseCase {

    List<NearPlaceResponseDto> getFestivalNearPlaces(Long festivalId);

    List<Place> getFestivalNearPlacesById(Long festivalId, List<String> placeIds);

    List<Place> cachePlaces(Long festivalId, Coordinate centerCoordinate);

}
