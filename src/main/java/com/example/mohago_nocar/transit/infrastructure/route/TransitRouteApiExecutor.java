package com.example.mohago_nocar.transit.infrastructure.route;

import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransitRouteApiExecutor {

    CompletableFuture<List<TransitRoute>> execute(final List<Location> locations);

}
