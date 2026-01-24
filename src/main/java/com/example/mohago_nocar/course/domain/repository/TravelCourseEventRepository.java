package com.example.mohago_nocar.course.domain.repository;

import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEvent;

import java.util.List;

public interface TravelCourseEventRepository {

    TravelCourseOptimizedEvent save(TravelCourseOptimizedEvent travelCourseOptimizedEvent);

    List<TravelCourseOptimizedEvent> findByStatusInOrderByCreatedDateAsc(
            List<EventProcessStatus> statuses, int size);

}
