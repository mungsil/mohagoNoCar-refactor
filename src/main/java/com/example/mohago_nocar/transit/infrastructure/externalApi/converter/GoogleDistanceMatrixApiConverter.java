package com.example.mohago_nocar.transit.infrastructure.externalApi.converter;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.infrastructure.externalApi.google.dto.response.GoogleDistanceMatrixResponse.Element;
import com.example.mohago_nocar.transit.infrastructure.externalApi.google.dto.response.RouteSpecification;
import com.example.mohago_nocar.transit.infrastructure.externalApi.google.dto.response.GoogleDistanceMatrixResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class GoogleDistanceMatrixApiConverter {

    public static Flux<RouteSpecification> convertToRouteSpecification(
            Mono<GoogleDistanceMatrixResponse> responseMono, Coordinate origin, List<Coordinate> destinations
    ) {
        return responseMono.flatMapMany(response ->
                Flux.range(0, destinations.size())
                        .map(index -> {
                            Element element = getDistanceAndDurationToDest(index, response);
                            return RouteSpecification.from(element, origin, destinations.get(index));
                        })
        );
    }

    private static Element getDistanceAndDurationToDest(Integer index, GoogleDistanceMatrixResponse response) {
        return response.rows().getFirst().elements().get(index);
    }

}
