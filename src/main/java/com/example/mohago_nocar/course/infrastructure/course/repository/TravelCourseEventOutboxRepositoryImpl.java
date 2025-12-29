package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.global.common.domain.OutboxStatus;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEventOutbox;
import com.example.mohago_nocar.course.domain.repository.TravelCourseEventOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TravelCourseEventOutboxRepositoryImpl implements TravelCourseEventOutboxRepository {

    private final TravelCourseOptimizedEventOutboxJpaRepository jpaRepository;

    @Override
    @Transactional
    public TravelCourseOptimizedEventOutbox save(TravelCourseOptimizedEventOutbox travelCourseOptimizedEventOutbox) {
        return jpaRepository.save(travelCourseOptimizedEventOutbox);
    }

    @Override
    public List<TravelCourseOptimizedEventOutbox> findByStatusInOrderByCreatedDateAsc(
            List<OutboxStatus> statuses, int size) {
        return jpaRepository.findTop10ByStatusInOrderByCreatedDateAsc(statuses, size);
    }

}
