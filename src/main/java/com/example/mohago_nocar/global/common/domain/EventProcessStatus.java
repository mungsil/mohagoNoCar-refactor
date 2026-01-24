package com.example.mohago_nocar.global.common.domain;

public enum EventProcessStatus {
    PENDING,        // 아직 처리 안됨
    PROCESSING,     // 처리 중
    FAIL,           // 처리 실패
    SENT            // 처리 완료
}
