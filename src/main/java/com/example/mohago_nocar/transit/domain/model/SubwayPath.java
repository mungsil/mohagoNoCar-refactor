package com.example.mohago_nocar.transit.domain.model;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import lombok.Getter;
import lombok.ToString;

import static com.example.mohago_nocar.transit.domain.model.PathType.SUBWAY;

@Getter
@ToString
public class SubwayPath extends SubPath{
    private final String subwayLineName;
    private final String startName;
    private final Coordinate startCoordinate;
    private final String endName;
    private final Coordinate endCoordinate;

    public SubwayPath(
            double distance,
            int sectionTime,
            String subwayLineName,
            String startName,
            Coordinate startCoordinate,
            String endName,
            Coordinate endCoordinate
    ) {
        super(distance, sectionTime);
        this.subwayLineName = subwayLineName;
        this.startName = startName;
        this.startCoordinate = startCoordinate;
        this.endName = endName;
        this.endCoordinate = endCoordinate;
    }

    @Override
    public PathType getPathType() {
        return SUBWAY;
    }

}
