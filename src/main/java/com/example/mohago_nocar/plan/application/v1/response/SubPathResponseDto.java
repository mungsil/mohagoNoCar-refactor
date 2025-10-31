package com.example.mohago_nocar.plan.application.v1.response;

import com.example.mohago_nocar.transit.domain.model.PathType;
import lombok.Getter;

@Getter
public abstract class SubPathResponseDto {
    protected final double distance;
    protected final int sectionTime;
    protected final PathType pathType;

    protected SubPathResponseDto(double distance, int sectionTime, PathType pathType) {
        this.distance = distance;
        this.sectionTime = sectionTime;
        this.pathType = pathType;
    }
}
