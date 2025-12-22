package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.application.dto.*;
import com.example.mohago_nocar.course.application.dto.RouteStepDto.LocationDto;
import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpot;
import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.domain.model.BusPath;
import com.example.mohago_nocar.transit.domain.model.SubwayPath;
import com.example.mohago_nocar.transit.domain.model.WalkPath;

import java.util.List;
import java.util.stream.Collectors;

public class TravelCourseConverter {

    public static RouteStepDto convertToRouteStepDto(
            final TravelSpot origin,
            final TravelSpot destination,
            final RouteStep step
    ){
        return new RouteStepDto(
                step.getTimeTakenMin(),
                step.getDistanceKm(),
                convertToLocationDto(origin),
                convertToLocationDto(destination),
                convertToSubPathDtos(step));
    }

    private static List<SubPathDto> convertToSubPathDtos(RouteStep step) {
        return step.getDetailPaths().stream()
                .map(path ->{
                    if (path instanceof WalkPath walk) {
                        return new WalkSubPathDto(walk.getDistanceKm(), walk.getTimeTakenMin(),
                                walk.getPathType().name());
                    }

                    if (path instanceof BusPath bus) {
                        return new BusSubPathDto(bus.getDistanceKm(), bus.getTimeTakenMin(),
                                bus.getPathType().name(), bus.getBusNo(), bus.getBusType(),
                                bus.getStartName(), bus.getStartCoordinate().getLongitude(), bus.getStartCoordinate().getLatitude(),
                                bus.getEndName(), bus.getEndCoordinate().getLongitude(), bus.getEndCoordinate().getLatitude());
                    }

                    if (path instanceof SubwayPath subway) {
                        return new SubwaySubPathDto(
                                subway.getDistanceKm(), subway.getTimeTakenMin(),
                                subway.getPathType().name(), subway.getSubwayLineName(),
                                subway.getStartName(), subway.getStartCoordinate().getLongitude(), subway.getStartCoordinate().getLatitude(),
                                subway.getEndName(), subway.getEndCoordinate().getLongitude(), subway.getEndCoordinate().getLatitude()
                        );
                    }

                    throw new IllegalArgumentException("지원하지 않는 경로 유형입니다.");
                })
                .collect(Collectors.toList());
    }

    private static LocationDto convertToLocationDto(TravelSpot origin) {
        Location location = origin.getLocation();
        return LocationDto.builder()
                .name(location.getName())
                .latitude(location.getCoordinate().getLatitude())
                .longitude(location.getCoordinate().getLongitude())
                .build();
    }

}
