package com.example.mohago_nocar.global.messaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DLQStatus {

    NEW("새로운 엔트리", "즉시 확인 필요"),
    POSSESSING("처리 중", "원인 분석 및 해결 중"),
    RESOLVED("해결 완료", "정상 처리 완료"),
    IGNORED("무시", "처리 불필요로 판단");

    private final String displayName;
    private final String description;

}
