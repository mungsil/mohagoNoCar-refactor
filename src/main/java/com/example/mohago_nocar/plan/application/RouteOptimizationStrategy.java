package com.example.mohago_nocar.plan.application;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.infrastructure.externalApi.google.dto.response.RouteSpecification;

import java.util.List;

public interface RouteOptimizationStrategy {

    List<Coordinate> calculateOptimalRoute(List<Coordinate> coordinates, List<RouteSpecification> routeSpecification);

}