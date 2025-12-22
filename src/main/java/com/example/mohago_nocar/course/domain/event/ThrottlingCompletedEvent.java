package com.example.mohago_nocar.course.domain.event;

import lombok.Getter;

@Getter
public class ThrottlingCompletedEvent {

    private final Long travelCourseId;

    public static ThrottlingCompletedEvent of(Long travelCourseId) {
        return new ThrottlingCompletedEvent(travelCourseId);
    }

    private ThrottlingCompletedEvent(Long travelCourseId) {
        this.travelCourseId = travelCourseId;
    }

}
