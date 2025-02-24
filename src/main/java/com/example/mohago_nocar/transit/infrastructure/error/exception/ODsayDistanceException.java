package com.example.mohago_nocar.transit.infrastructure.error.exception;

import com.example.mohago_nocar.global.common.exception.CustomException;
import com.example.mohago_nocar.global.common.exception.Status;

public class ODsayDistanceException extends CustomException {

    public ODsayDistanceException(Status status) {
        super(status);
    }

}
