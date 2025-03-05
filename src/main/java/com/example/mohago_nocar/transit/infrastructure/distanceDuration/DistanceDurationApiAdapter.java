package com.example.mohago_nocar.transit.infrastructure.distanceDuration;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.domain.model.RouteMetrics;

import java.util.List;

public interface DistanceDurationApiAdapter {

    List<RouteMetrics> getDistanceAndDuration(Coordinate origin, List<Coordinate> destinations);

}
