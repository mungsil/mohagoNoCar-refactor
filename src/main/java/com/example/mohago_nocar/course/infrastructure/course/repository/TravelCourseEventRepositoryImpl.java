package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.repository.TravelCourseEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TravelCourseEventRepositoryImpl implements TravelCourseEventRepository {

    private final TravelCourseOptimizedEventJpaRepository jpaRepository;

    @Override
    @Transactional
    public TravelCourseOptimizedEvent save(TravelCourseOptimizedEvent travelCourseOptimizedEvent) {
        return jpaRepository.save(travelCourseOptimizedEvent);
    }

    @Override
    public List<TravelCourseOptimizedEvent> findByStatusInOrderByCreatedDateAsc(
            List<EventProcessStatus> statuses, int size) {
        return jpaRepository.findTop10ByStatusInOrderByCreatedDateAsc(statuses, size);
    }

}
