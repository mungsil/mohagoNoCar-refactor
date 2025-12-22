package com.example.mohago_nocar.plan.presentation.exception;

import com.example.mohago_nocar.global.common.exception.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PlanErrorCode implements Status {

    TRAVEL_DATE_NOT_IN_FESTIVAL_PERIOD(HttpStatus.BAD_REQUEST, "PLAN400", "여행 날짜가 축제 기간을 벗어났습니다"),
    BATCH_TASK_NOT_FOUND(HttpStatus.BAD_REQUEST, "BATCH_TASK_NOT_FOUND", "존재하지 않는 배치 작업입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
