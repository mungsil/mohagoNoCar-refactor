package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEventHandleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelCourseOptimizedEventHandleHistoryJpaRepository extends
        JpaRepository<TravelCourseOptimizedEventHandleHistory, Long> {

}
