package com.example.mohago_nocar.transit.infrastructure.route.odsay.deprecated;

import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Deprecated
public interface TransitRouteApiExecutor {

    CompletableFuture<List<TransitRoute>> execute(final List<Location> locations);

}
