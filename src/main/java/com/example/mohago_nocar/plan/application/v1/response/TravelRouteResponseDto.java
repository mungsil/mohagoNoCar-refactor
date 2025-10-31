package com.example.mohago_nocar.plan.application.v1.response;

import com.example.mohago_nocar.transit.domain.model.TransitRoute;
import lombok.Builder;

import java.util.List;

@Builder
public record TravelRouteResponseDto(
        int totalTime,
        double totalDistance,
        Location origin,
        Location destination,
        List<SubPathResponseDto> subPaths
) {

    public static TravelRouteResponseDto of(TransitRoute transitRoute) {
        return TravelRouteResponseDto.builder()
                .origin(Location.builder()
                        .latitude(transitRoute.getOrigin().getCoordinate().getLatitude())
                        .longitude(transitRoute.getOrigin().getCoordinate().getLongitude())
                        .name(transitRoute.getOrigin().getName())
                        .build())
                .destination(Location.builder()
                        .latitude(transitRoute.getDestination().getCoordinate().getLatitude())
                        .longitude(transitRoute.getDestination().getCoordinate().getLongitude())
                        .name(transitRoute.getDestination().getName())
                        .build())
                .totalTime(transitRoute.getTotalTime())
                .totalDistance(transitRoute.getTotalDistance())
                .subPaths(transitRoute.getSubPaths().stream()
                        .map(subPath ->
                                switch (subPath.getPathType()) {
                                    case BUS -> BusPathResponseDto.of(subPath);
                                    case WALK -> WalkPathResponseDto.of(subPath);
                                    case SUBWAY -> SubwayPathResponseDto.of(subPath);
                                })
                        .toList()
                )
                .build();

    }

    @Builder
    private record Location(
            String name,
            Double longitude,
            Double latitude
    ){

    }

}
