package com.example.mohago_nocar.transit.infrastructure.route;

import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;

public interface TransitRouteApiAdapter {

    TransitRoute getTransitRouteBetweenLocations(Location origin, Location destination);

}
