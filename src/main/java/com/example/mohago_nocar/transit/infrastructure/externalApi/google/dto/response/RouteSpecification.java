package com.example.mohago_nocar.transit.infrastructure.externalApi.google.dto.response;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import lombok.Builder;

/**
 *
 * @param distanceInKm
 * @param durationInMinutes
 * @param origin
 * @param destination
 */
@Builder
public record RouteSpecification(
        Double distanceInKm,
        Long durationInMinutes,
        Coordinate origin,
        Coordinate destination
) {

    public static RouteSpecification from(
            GoogleDistanceMatrixResponse.Element element,
            Coordinate origin,
            Coordinate destination
    ) {
        return RouteSpecification.builder()
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
