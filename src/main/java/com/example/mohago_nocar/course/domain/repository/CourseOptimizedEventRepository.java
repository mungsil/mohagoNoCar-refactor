package com.example.mohago_nocar.course.domain.repository;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;

import java.util.List;
import java.util.Optional;

public interface CourseOptimizedEventRepository {

    CourseOptimizedEvent save(CourseOptimizedEvent event);

    List<CourseOptimizedEvent> findTopNByStatusInOrderByCreatedDateAsc(int size, List<EventProcessStatus> eventProcessStatus);

    Optional<CourseOptimizedEvent> findByCourseId(Long travelCourseId);

}
