package com.example.mohago_nocar.course.application.dto;

public record SubwaySubPathDto(
        double distanceKm,
        int timeTakenMin,
        String pathType,
        String subwayLineName,
        String startPlaceName,
        double startLongitude,
        double startLatitude,
        String endPlaceName,
        double endLongitude,
        double endLatitude
) implements SubPathDto{

}
