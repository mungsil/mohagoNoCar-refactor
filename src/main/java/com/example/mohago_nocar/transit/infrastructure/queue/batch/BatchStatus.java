package com.example.mohago_nocar.transit.infrastructure.queue.batch;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BatchStatus {

    PENDING("실행 대기 중"),
    RUNNING("실행 중"),
    COMPLETED("완료"),
    FAILED("실패");

    private final String description;

}

