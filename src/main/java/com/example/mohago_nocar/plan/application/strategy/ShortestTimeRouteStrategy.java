package com.example.mohago_nocar.plan.application.strategy;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.global.common.exception.InternalServerException;
import com.example.mohago_nocar.transit.domain.model.RouteMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class ShortestTimeRouteStrategy implements RouteOptimizationStrategy {

    private static final int FIRST = 0;

    @Override
    public List<Coordinate> calculateOptimalRoute(List<Coordinate> coordinates, List<RouteMetrics> routeMetrics) {
        int locationCount = coordinates.size();
        Map<Coordinate, Map<Coordinate, RouteMetrics>> fromToTransitInfoMap = new HashMap<>();

        for (int originIndex = FIRST; originIndex < locationCount; originIndex++) {
            Map<Coordinate, RouteMetrics> toLocationTransitInfoMap = new HashMap<>();

            for (int destinationIndex = FIRST; destinationIndex < locationCount; destinationIndex++) {

                if (originIndex == destinationIndex) {
                    continue;
                }

                Coordinate origin = coordinates.get(originIndex);
                Coordinate destination = coordinates.get(destinationIndex);

                RouteMetrics routeSpec = getMatchedRouteSpec(origin, destination, routeMetrics);
                toLocationTransitInfoMap.put(destination, routeSpec);
            }

            fromToTransitInfoMap.put(coordinates.get(originIndex), toLocationTransitInfoMap);
        }

        List<Coordinate> route = new ArrayList<>();
        List<Boolean> isSelected = new ArrayList<>();
        List<Coordinate> optimalRoute = new ArrayList<>();
        for (int i = 0; i < locationCount; i++) {
            isSelected.add(false);
            optimalRoute.add(coordinates.get(i));
        }

        routeBacktracking(0, coordinates, fromToTransitInfoMap , optimalRoute, route, isSelected);
        return optimalRoute;
    }

    private RouteMetrics getMatchedRouteSpec(
            Coordinate origin,
            Coordinate destination,
            List<RouteMetrics> routeMetricsBetweenLocations
    ) {
        Optional<RouteMetrics> routeMetrics = routeMetricsBetweenLocations.stream()
                .filter(route -> route.isEqualLocation(origin, destination))
                .findFirst();

        if (routeMetrics.isEmpty()) {
            log.error("origin-{}, destination-{}를 가지는 RouteSpec을 찾을 수 없습니다.", origin, destination);
            throw new InternalServerException();
        }

        return routeMetrics.get();
    }

    private void routeBacktracking(
            int k,
            List<Coordinate> coordinates,
            Map<Coordinate, Map<Coordinate, RouteMetrics>> transitMaps,
            List<Coordinate> optimal,
            List<Coordinate> route,
            List<Boolean> isSelected
    ) {
        int n = coordinates.size();

        if (k == n)
        {
            int t1 = calcTravelTime(optimal, transitMaps);
            int t2 = calcTravelTime(route, transitMaps);

            if (t1 > t2) {
                for (int i = 0; i < n; i++) {
                    optimal.set(i, route.get(i));
                }
            }
            return;
        }

        for (int i = 0; i < n; i++) {
            if (isSelected.get(i)) {
                continue;
            }

            isSelected.set(i, true);
            route.add(coordinates.get(i));
            routeBacktracking(k + 1, coordinates, transitMaps, optimal, route, isSelected);
            route.removeLast();
            isSelected.set(i, false);
        }
    }

    private int calcTravelTime(List<Coordinate> route,  Map<Coordinate, Map<Coordinate, RouteMetrics>> routeMaps) {
        int n = route.size();

        int travelTime = 0;
        for (int i = 0; i < n - 1; i++) {
            travelTime += routeMaps.get(route.get(i)).get(route.get(i + 1)).durationInMinutes();
        }
        return travelTime;
    }

}
