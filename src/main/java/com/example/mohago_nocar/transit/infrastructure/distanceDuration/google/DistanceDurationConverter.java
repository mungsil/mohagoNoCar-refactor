package com.example.mohago_nocar.transit.infrastructure.distanceDuration.google;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.domain.model.RouteMetrics;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.dto.response.GoogleDistanceMatrixResponse;

import java.util.List;
import java.util.stream.IntStream;

public class DistanceDurationConverter {

    public static List<RouteMetrics> convertMatrixToRouteMetrics(
            GoogleDistanceMatrixResponse distanceMatrix,
            Coordinate origin,
            List<Coordinate> destinations
    ) {

        return IntStream.range(0, destinations.size())
                .mapToObj(visit -> RouteMetrics.of(
                        distanceMatrix.rows().getFirst().elements().get(visit),
                        origin,
                        destinations.get(visit)))
                .toList();
    }

}
