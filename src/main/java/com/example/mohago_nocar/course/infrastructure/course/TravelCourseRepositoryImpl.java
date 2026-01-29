package com.example.mohago_nocar.course.infrastructure.course;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.domain.repository.TravelCourseRepository;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public Optional<AnonymousUser> findUserByCourseId(Long travelCourseId) {
        return travelCourseJpaRepository.findUserByCourseId(travelCourseId);
    }

    @Override
    public Optional<CourseOptimizedEvent> findOptimizedEventByCourseId(Long travelCourseId) {
        return travelCourseJpaRepository.findOptimizedEventByCourseId(travelCourseId);
    }

}
