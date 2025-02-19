package com.example.mohago_nocar.transit.domain.model;

import com.example.mohago_nocar.plan.domain.model.Location;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class TransitRoute {

    private final Location origin;
    private final Location destination;
    private final int totalTime;
    private final double totalDistance;
    private final List<SubPath> subPaths;

    public static TransitRoute from(Location origin, Location destination, int totalTime, double totalDistance, List<SubPath> subPaths) {
        return TransitRoute.builder()
                .origin(origin)
                .destination(destination)
                .totalTime(totalTime)
                .totalDistance(totalDistance)
                .subPaths(subPaths)
                .build();
    }

    @Override
    public String toString() {
        List<String> collect = subPaths.stream().map(Object::toString).toList();

        return "TransitInfo{" +
                "totalDistance=" + totalDistance +
                ", subPaths=" + collect +
                '}';
    }

    @Builder
    private TransitRoute(Location origin, Location destination, int totalTime, double totalDistance, List<SubPath> subPaths) {
        this.origin = origin;
        this.destination = destination;
        this.totalTime = totalTime;
        this.totalDistance = totalDistance;
        this.subPaths = subPaths;
    }

}
