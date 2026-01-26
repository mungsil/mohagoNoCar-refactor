package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.repository.CourseOptimizedEventRepository;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CourseOptimizedEventRepositoryImpl implements CourseOptimizedEventRepository {

    private final CourseOptimizedEventJpaRepository jpaRepository;

    @Override
    public CourseOptimizedEvent save(CourseOptimizedEvent event) {
        return jpaRepository.save(event);
    }

    @Override
    public List<CourseOptimizedEvent> findTop10ByStatusInOrderByCreatedDateAsc(int size, List<EventProcessStatus> eventProcessStatus) {
        return jpaRepository.findTop10ByStatusInOrderByCreatedDateAsc(size, eventProcessStatus);
    }

}
