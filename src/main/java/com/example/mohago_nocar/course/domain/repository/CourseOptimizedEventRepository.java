package com.example.mohago_nocar.course.domain.repository;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;

import java.util.List;

public interface CourseOptimizedEventRepository {

    CourseOptimizedEvent save(CourseOptimizedEvent event);

    List<CourseOptimizedEvent> findTop10ByStatusInOrderByCreatedDateAsc(int size, List<EventProcessStatus> eventProcessStatus);

}
