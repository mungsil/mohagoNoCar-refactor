package com.example.mohago_nocar.course.domain.service;

import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpot;
import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.place.domain.model.Place;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TravelSpotUseCase {

    Set<TravelSpot> createUnOrderedTravelSpots(TravelCourse course, Festival festival, List<Place> places);

    Set<TravelSpot> determineOptimizedTravelOrder(Set<TravelSpot> unorderedSpots);

    List<TravelSpot> saveAll(Set<TravelSpot> spotsWithOrder);

    List<TravelSpot> getByCourseId(Long travelCourseId);

    Set<TravelSpot> makeSpotsWithoutOrder(TravelCourse ownerCourse, Long festivalId, LocalDate travelDate, List<String> placeIds);
}
