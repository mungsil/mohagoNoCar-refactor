package com.example.mohago_nocar.transit.domain.model;

import com.example.mohago_nocar.plan.domain.model.Location;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class TransitRoute {

    private final int totalTime;
    private final double totalDistance;
    private final List<SubPath> subPaths;
    private final Location origin;
    private final Location destination;

    public static TransitRoute from(Location origin, Location destination, int totalTime, double totalDistance, List<SubPath> subPaths) {
        return TransitRoute.builder()
                .origin(origin)
                .destination(destination)
                .totalTime(totalTime)
                .totalDistance(totalDistance)
                .subPaths(subPaths)
                .build();
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
