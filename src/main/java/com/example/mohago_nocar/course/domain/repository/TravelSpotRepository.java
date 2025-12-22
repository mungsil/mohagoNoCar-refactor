package com.example.mohago_nocar.course.domain.repository;

import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpot;

import java.util.Collection;
import java.util.List;

public interface TravelSpotRepository {
    TravelSpot save(TravelSpot travelSpot);

    // todo 배치 insert 지원 시 Jpa 기본 saveAll 사용
    List<TravelSpot> saveAll(Collection<TravelSpot> travelSpots);

    List<TravelSpot> findByTravelCourseId(Long travelCourseId);

}
