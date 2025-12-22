package com.example.mohago_nocar.course.application.dto;

import com.example.mohago_nocar.transit.domain.model.PathType;

public record WalkSubPathDto(
        double distanceKm,
        int timeTakenMin,
        String pathType
) implements SubPathDto {


}
