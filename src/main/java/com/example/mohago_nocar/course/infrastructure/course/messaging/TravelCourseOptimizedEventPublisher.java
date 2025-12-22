package com.example.mohago_nocar.course.infrastructure.course.messaging;

import com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent;

public interface TravelCourseOptimizedEventPublisher {

    void publish(TravelCourseOptimizedEvent event);

}
