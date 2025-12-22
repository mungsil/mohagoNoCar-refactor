package com.example.mohago_nocar.transit.infrastructure.error.exception;

import com.example.mohago_nocar.global.common.exception.Status;
import com.example.mohago_nocar.transit.infrastructure.error.code.OdsayErrorCode;
import lombok.Getter;

@Getter
public class ODsayRouteException extends RuntimeException  {

    private final OdsayErrorCode errorCode;

    public ODsayRouteException(OdsayErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
