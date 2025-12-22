package com.example.mohago_nocar.course.application.dto;

import lombok.Builder;

import java.util.List;

public record RouteStepDto (
        int sectionTimeMin,
        double sectionDistanceKm,
        LocationDto origin,
        LocationDto destination,
        List<SubPathDto> subPaths
){

    public static RouteStepDto of(
            int sectionTimeMin,
            double sectionDistanceKm,
            LocationDto origin,
            LocationDto destination,
            List<SubPathDto> subPaths
    ) {
        return new RouteStepDto(sectionTimeMin, sectionDistanceKm, origin, destination, subPaths);
    }

    @Builder
    public record LocationDto(
            String name,
            Double longitude,
            Double latitude
    ){
    }

}
