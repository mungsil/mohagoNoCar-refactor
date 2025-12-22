package com.example.mohago_nocar.course.application;

import com.example.mohago_nocar.global.common.exception.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CourseErrorCode implements Status {
    TRAVEL_COURSE_OPTIMIZATION_INCOMPLETE(HttpStatus.BAD_REQUEST, "TRAVEL_COURSE_OPTIMIZATION_INCOMPLETE", "여행 코스 최적화가 완료되지 않았습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
