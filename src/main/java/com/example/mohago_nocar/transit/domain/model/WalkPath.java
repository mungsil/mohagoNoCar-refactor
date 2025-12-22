package com.example.mohago_nocar.transit.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString(callSuper = true)
public class WalkPath extends SubPath{

    public WalkPath(double distanceKm, int timeTakenMin) {
        super(distanceKm, timeTakenMin, PathType.WALK);
    }

}
