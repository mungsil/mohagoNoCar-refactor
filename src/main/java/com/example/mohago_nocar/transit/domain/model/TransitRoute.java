package com.example.mohago_nocar.transit.domain.model;

import com.example.mohago_nocar.plan.domain.model.Location;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@NoArgsConstructor
@Getter
@ToString
public class TransitRoute {

    private int totalTime;

    private double totalDistance;

    private List<SubPath> subPaths;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "start_name")),
            @AttributeOverride(name = "coordinate.latitude", column = @Column(name = "start_latitude")),
            @AttributeOverride(name = "coordinate.longitude", column = @Column(name = "start_longitude"))
    })
    private Location origin;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "end_name")),
            @AttributeOverride(name = "coordinate.latitude", column = @Column(name = "end_latitude")),
            @AttributeOverride(name = "coordinate.longitude", column = @Column(name = "end_longitude"))
    })
    private Location destination;

    public static TransitRoute from(Location origin, Location destination,
                                    int totalTime, double totalDistance, List<SubPath> subPaths) {
        TransitRoute route = TransitRoute.builder()
                .origin(origin)
                .destination(destination)
                .totalTime(totalTime)
                .totalDistance(totalDistance)
                .subPaths(subPaths)
                .build();

        return route;
    }

    @Builder(access = AccessLevel.PRIVATE)
    private TransitRoute(Location origin, Location destination, int totalTime, double totalDistance, List<SubPath> subPaths) {
        this.origin = origin;
        this.destination = destination;
        this.totalTime = totalTime;
        this.totalDistance = totalDistance;
        this.subPaths = subPaths;
    }

}
