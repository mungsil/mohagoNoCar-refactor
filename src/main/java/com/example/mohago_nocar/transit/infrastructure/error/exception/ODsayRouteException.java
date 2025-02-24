package com.example.mohago_nocar.transit.infrastructure.error.exception;

import com.example.mohago_nocar.global.common.exception.CustomException;
import com.example.mohago_nocar.global.common.exception.Status;

public class ODsayRouteException extends CustomException {

    public ODsayRouteException(Status status) {
        super(status);
    }

}
