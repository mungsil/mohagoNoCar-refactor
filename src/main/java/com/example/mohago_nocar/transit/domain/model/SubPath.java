package com.example.mohago_nocar.transit.domain.model;

import lombok.Getter;

@Getter
public abstract class SubPath {
    protected final double distanceKm; // 구간 거리
    protected final int sectionTimeMin; // 구간 소요 시간

    protected SubPath(double distanceKm, int sectionTimeMin) {
        this.distanceKm = distanceKm;
        this.sectionTimeMin = sectionTimeMin;
    }

    public abstract PathType getPathType();
}
