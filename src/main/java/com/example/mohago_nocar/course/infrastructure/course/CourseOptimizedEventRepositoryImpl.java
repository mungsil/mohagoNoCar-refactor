package com.example.mohago_nocar.course.infrastructure.course;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.repository.CourseOptimizedEventRepository;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CourseOptimizedEventRepositoryImpl implements CourseOptimizedEventRepository {

    private final CourseOptimizedEventJpaRepository jpaRepository;

    @Override
    public CourseOptimizedEvent save(CourseOptimizedEvent event) {
        return jpaRepository.save(event);
    }

    @Override
    public List<CourseOptimizedEvent> findTopNByStatusInOrderByCreatedDateAsc(int size, List<EventProcessStatus> eventProcessStatus) {
        return jpaRepository.findTopNByStatusInOrderByCreatedDateAsc(size, eventProcessStatus);
    }

    @Override
    public Optional<CourseOptimizedEvent> findByCourseId(Long travelCourseId) {
        return jpaRepository.findById(travelCourseId);
    }

}
