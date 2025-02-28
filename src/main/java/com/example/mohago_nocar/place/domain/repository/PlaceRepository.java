package com.example.mohago_nocar.place.domain.repository;

import com.example.mohago_nocar.place.domain.model.Place;

import java.util.List;

public interface PlaceRepository {

    List<Place> getChosenPlace(Long festivalId, List<String> placeIds);

    List<Place> getFestivalAroundPlaces(Long festivalId);

    List<Place> savePlaces(Long festivalId, List<Place> nearPlaces);
}
