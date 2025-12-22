package com.example.mohago_nocar.course.domain.repository;

import com.example.mohago_nocar.course.application.dto.GetRequesterInfoDto;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TravelCourseRepository {

    TravelCourse save(TravelCourse course);

    Optional<TravelCourse> findById(Long travelCourseId);

    Optional<GetRequesterInfoDto> getRequestrInfo(Long travelCourseId);

    List<TravelCourse> findOutdatedCoursesNeedingNotification(LocalDateTime thresholdTime, Boolean notificationSent);

}

