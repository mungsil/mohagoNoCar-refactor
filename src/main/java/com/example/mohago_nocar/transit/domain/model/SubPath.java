package com.example.mohago_nocar.transit.domain.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "pathType",
        visible = true
)
@JsonSubTypes({
        @Type(value = BusPath.class, name = "BUS"),
        @Type(value = SubwayPath.class, name = "SUBWAY"),
        @Type(value = WalkPath.class, name = "WALK"),
})
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class SubPath {

    private double distanceKm; // 구간 거리

    private int timeTakenMin; // 구간 소요 시간

    private PathType pathType;

    protected SubPath(double distanceKm, int timeTakenMin, PathType pathType) {
        this.distanceKm = distanceKm;
        this.timeTakenMin = timeTakenMin;
        this.pathType = pathType;
    }

}
