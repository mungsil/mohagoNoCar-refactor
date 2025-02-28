package com.example.mohago_nocar.place.domain.service;

import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.place.domain.model.Place;
import com.example.mohago_nocar.place.presentation.response.NearPlaceResponseDto;

import java.util.List;

public interface PlaceUseCase {

    List<NearPlaceResponseDto> getPlacesAround(Long festivalId);

    List<Place> getSelectedPlacesAround(Festival festival, List<String> selectedIds);

}
