package com.example.mohago_nocar.course.application.dto;

public record BusSubPathDto(
        double distanceKm,
        int timeTakenMin,
        String pathType,
        String busNo,
        int busType,
        String startPlaceName,
        double startLongitude,
        double startLatitude,
        String endPlaceName,
        double endLongitude,
        double endLatitude
) implements SubPathDto{

}
