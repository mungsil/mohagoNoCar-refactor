package com.example.mohago_nocar.course.domain.model.course;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TravelCourseStatus {
    PENDING("처리 대기 중"),
    SUCCEEDED("처리 성공"),
    FAILED("처리 실패");

    private final String description;

    public boolean isComplete() {
        return this == SUCCEEDED || this == FAILED;
    }

}