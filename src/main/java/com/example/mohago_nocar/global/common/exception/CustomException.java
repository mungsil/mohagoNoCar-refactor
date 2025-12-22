package com.example.mohago_nocar.global.common.exception;

import lombok.Getter;

// todo: businessException으로 이름 변경
@Getter
public class CustomException extends RuntimeException {

    private Status status;

    public CustomException(Status status) {
        super(status.getMessage());
        this.status = status;
    }

    public CustomException(String message, Status status) {
        super(message);
        this.status = status;
    }
}