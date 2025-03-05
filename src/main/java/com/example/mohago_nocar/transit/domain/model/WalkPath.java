package com.example.mohago_nocar.transit.domain.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WalkPath extends SubPath{

    public WalkPath(double distance, int sectionTime) {
        super(distance, sectionTime);
    }

    @Override
    public PathType getPathType() {
        return PathType.WALK;
    }

}
