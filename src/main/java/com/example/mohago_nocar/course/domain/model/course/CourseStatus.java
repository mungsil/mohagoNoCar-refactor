package com.example.mohago_nocar.course.domain.model.course;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CourseStatus {

    ENQUEUED("처리 대기 중"),
    SUCCEEDED("처리 성공"),
    FAILED("처리 실패"),
    WAITING_REPROCESSING("재처리 대기 중");

    private final String description;

}
