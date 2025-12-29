package com.example.mohago_nocar.course.domain.repository;

import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEventHandleHistory;
import org.springframework.stereotype.Repository;

public interface TravelCourseOptimizedEventHandleHistoryRepository {
    TravelCourseOptimizedEventHandleHistory save(TravelCourseOptimizedEventHandleHistory handleHistory);
}
