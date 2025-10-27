package com.example.mohago_nocar.transit.infrastructure.route.odsay.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ODsayTransitRouteResponseDeserializer.class)
public abstract class ODsayTransitRouteResponse {

    public abstract Boolean isValid();

}
