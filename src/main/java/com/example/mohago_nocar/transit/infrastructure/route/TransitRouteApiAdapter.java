package com.example.mohago_nocar.transit.infrastructure.route;

import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;

import java.util.concurrent.CompletableFuture;

public interface TransitRouteApiAdapter {

    TransitRoute getTransitRouteBetweenLocations(Location origin, Location destination);

    CompletableFuture<TransitRoute> getTransitRouteWithThrottling(Location origin, Location destination);

}
