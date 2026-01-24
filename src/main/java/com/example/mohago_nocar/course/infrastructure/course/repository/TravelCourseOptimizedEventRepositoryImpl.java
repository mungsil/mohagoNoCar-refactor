package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.course.domain.repository.TravelCourseOptimizedEventRepository;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEventLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TravelCourseOptimizedEventRepositoryImpl implements TravelCourseOptimizedEventRepository {

    private final TravelCourseOptimizedEventJpaRepository jpaRepository;

    @Override
    @Transactional
    public TravelCourseOptimizedEventLog save(TravelCourseOptimizedEventLog travelCourseOptimizedEventLog) {
        return jpaRepository.save(travelCourseOptimizedEventLog);
    }

    @Override
    public List<TravelCourseOptimizedEventLog> findByStatusInOrderByCreatedDateAsc(
            List<EventProcessStatus> statuses, int size) {
        return jpaRepository.findTop10ByStatusInOrderByCreatedDateAsc(statuses, size);
    }

}
