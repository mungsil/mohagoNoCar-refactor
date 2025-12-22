package com.example.mohago_nocar.transit.domain.model;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.example.mohago_nocar.transit.domain.model.PathType.SUBWAY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString(callSuper = true)
public class SubwayPath extends SubPath{

    private String subwayLineName;

    private String startName;

    private Coordinate startCoordinate;

    private String endName;

    private Coordinate endCoordinate;

    public SubwayPath(
            double distanceKm,
            int timeTakenMin,
            String subwayLineName,
            String startName,
            Coordinate startCoordinate,
            String endName,
            Coordinate endCoordinate
    ) {
        super(distanceKm, timeTakenMin, SUBWAY);
        this.subwayLineName = subwayLineName;
        this.startName = startName;
        this.startCoordinate = startCoordinate;
        this.endName = endName;
        this.endCoordinate = endCoordinate;
    }

}

