package com.example.mohago_nocar.transit.infrastructure.distanceDuration;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.domain.model.RouteMetrics;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

// todo 이름 변경해도 될듯
public interface DistanceDurationApiAdapter {

    List<RouteMetrics> getDistanceAndDuration(Coordinate origin, List<Coordinate> destinations);

    CompletableFuture<List<RouteMetrics>> getDistanceAndDurationAsync(Coordinate origin, Set<Coordinate> destinations);
}
