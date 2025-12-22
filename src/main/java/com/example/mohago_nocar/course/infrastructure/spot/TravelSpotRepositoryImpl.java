package com.example.mohago_nocar.course.infrastructure.spot;

import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpot;
import com.example.mohago_nocar.course.domain.repository.TravelSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TravelSpotRepositoryImpl implements TravelSpotRepository {

    private final TravelSpotJpaRepository travelSpotJpaRepository;

    @Override
    public TravelSpot save(TravelSpot travelSpot) {
        return travelSpotJpaRepository.save(travelSpot);
    }

    // todo 배치 insert 지원 시 Jpa 기본 saveAll 사용
    @Override
    public List<TravelSpot> saveAll(Collection<TravelSpot> travelSpots) {
        return travelSpotJpaRepository.saveAll(travelSpots);
    }

    @Override
    public List<TravelSpot> findByTravelCourseId(Long travelCourseId) {
        return travelSpotJpaRepository.findByCourseId(travelCourseId);
    }

}
