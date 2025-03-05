package com.example.mohago_nocar.transit.domain.model;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.dto.response.GoogleDistanceMatrixResponse;
import lombok.Builder;

/**
 *
 * @param distanceInKm
 * @param durationInMinutes
 * @param origin
 * @param destination
 */
@Builder
public record RouteMetrics(
        Double distanceInKm,
        Long durationInMinutes,
        Coordinate origin,
        Coordinate destination
) {

    public static RouteMetrics of(
            GoogleDistanceMatrixResponse.Element element,
            Coordinate origin,
            Coordinate destination
    ) {
        return RouteMetrics.builder()
                .distanceInKm(element.distance().value() / 1000.0)
                .durationInMinutes(element.duration().value() / 60L)
                .origin(origin)
                .destination(destination)
                .build();
    }

    public boolean isEqualLocation(Coordinate origin, Coordinate destination) {
        return this.origin.equals(origin) && this.destination.equals(destination);
    }

}
