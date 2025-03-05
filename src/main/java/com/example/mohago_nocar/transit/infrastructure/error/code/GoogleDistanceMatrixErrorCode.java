package com.example.mohago_nocar.transit.infrastructure.error.code;

import com.example.mohago_nocar.global.common.exception.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GoogleDistanceMatrixErrorCode implements Status {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "GOOGLE400", "잘못된 요청입니다"),
    QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "GOOGLE429", "할당량이 초과되었습니다"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GOOGLE500", "외부 서버 오류가 발생했습니다"),
    API_KEY_INVALID(HttpStatus.UNAUTHORIZED, "GOOGLE401 ", "유효하지 않은 API 키입니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
