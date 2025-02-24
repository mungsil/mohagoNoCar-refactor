package com.example.mohago_nocar.transit.infrastructure.route.odsay.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class ODsayRouteInvalidResponse extends ODsayTransitRouteResponse {

    private final String errorCode;
    private final String errorMessage;

    @Override
    public Boolean isValid() {
        return false;
    }

}

