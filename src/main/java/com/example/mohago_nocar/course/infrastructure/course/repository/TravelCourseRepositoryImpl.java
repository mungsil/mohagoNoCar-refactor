package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.course.application.dto.GetRequesterInfoDto;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.domain.repository.TravelCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TravelCourseRepositoryImpl implements TravelCourseRepository {

    private final TravelCourseJpaRepository travelCourseJpaRepository;

    @Override
    public TravelCourse save(TravelCourse course) {
        return travelCourseJpaRepository.save(course);
    }

    @Override
    public Optional<TravelCourse> findById(Long travelCourseId) {
        return travelCourseJpaRepository.findById(travelCourseId);
    }

    @Override
    public Optional<GetRequesterInfoDto> getRequestrInfo(Long travelCourseId) {
        return travelCourseJpaRepository.findRequesterInfoByTravelCourseId(travelCourseId);
    }

    @Override
    public List<TravelCourse> findOutdatedCoursesNeedingNotification(LocalDateTime thresholdTime, Boolean notificationSent) {
        return travelCourseJpaRepository.findOutdatedCoursesNeedingNotification(thresholdTime, Boolean.FALSE);
    }

}
