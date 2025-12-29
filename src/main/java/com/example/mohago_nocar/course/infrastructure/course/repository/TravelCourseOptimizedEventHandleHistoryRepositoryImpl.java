package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEventHandleHistory;
import com.example.mohago_nocar.course.domain.repository.TravelCourseOptimizedEventHandleHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class TravelCourseOptimizedEventHandleHistoryRepositoryImpl
        implements TravelCourseOptimizedEventHandleHistoryRepository {

    private final TravelCourseOptimizedEventHandleHistoryJpaRepository jpaRepository;

    @Override
    @Transactional
    public TravelCourseOptimizedEventHandleHistory save(TravelCourseOptimizedEventHandleHistory handleHistory) {
        return jpaRepository.save(handleHistory);
    }

}
