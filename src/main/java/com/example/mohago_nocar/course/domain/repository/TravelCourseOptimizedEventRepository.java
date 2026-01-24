package com.example.mohago_nocar.course.domain.repository;

import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEventLog;

import java.util.List;

public interface TravelCourseOptimizedEventRepository {

    TravelCourseOptimizedEventLog save(TravelCourseOptimizedEventLog travelCourseOptimizedEventLog);

    List<TravelCourseOptimizedEventLog> findByStatusInOrderByCreatedDateAsc(
            List<EventProcessStatus> statuses, int size);

}
