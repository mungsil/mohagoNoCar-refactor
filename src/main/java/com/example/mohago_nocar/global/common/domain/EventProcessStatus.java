package com.example.mohago_nocar.global.common.domain;

/**
 * 이벤트 처리 상태를 표현합니다.
 */
public enum EventProcessStatus {
    CREATED,        // 아직 처리 안됨
    PROCESSING,     // 처리 중
    RETRYABLE_FAIL, // 처리 실패, 재시도 가능.
    FATAL_FAIL,     // 처리 실패, 치명적인 원인으로 재시도 불가능
    SUCCESS,         // 처리 완료
    PENDING_RETRY,      // 재처리 대기
}
