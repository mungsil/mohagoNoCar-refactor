package com.example.mohago_nocar.plan.application.v1.strategy;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.domain.model.RouteMetrics;

import java.util.List;

public interface RouteOptimizationStrategy {

    List<? extends Coordinate> calculateOptimalRoute(List<Coordinate> coordinates, List<RouteMetrics> routeMetrics);

}