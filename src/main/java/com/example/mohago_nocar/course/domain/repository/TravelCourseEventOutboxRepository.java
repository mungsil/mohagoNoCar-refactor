package com.example.mohago_nocar.course.domain.repository;

import com.example.mohago_nocar.global.common.domain.OutboxStatus;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEventOutbox;

import java.util.List;

public interface TravelCourseEventOutboxRepository {

    TravelCourseOptimizedEventOutbox save(TravelCourseOptimizedEventOutbox travelCourseOptimizedEventOutbox);

    List<TravelCourseOptimizedEventOutbox> findByStatusInOrderByCreatedDateAsc(
            List<OutboxStatus> statuses, int size);

}
