package com.example.mohago_nocar.course.domain.repository;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.user.domain.AnonymousUser;

import java.util.Optional;

public interface TravelCourseRepository {

    TravelCourse save(TravelCourse course);

    Optional<TravelCourse> findById(Long travelCourseId);

    Optional<AnonymousUser> findUserByCourseId(Long travelCourseId);

    Optional<CourseOptimizedEvent> findOptimizedEventByCourseId(Long travelCourseId);
}

