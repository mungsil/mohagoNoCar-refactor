package com.example.mohago_nocar.transit.domain.model;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import lombok.Getter;
import lombok.ToString;

import static com.example.mohago_nocar.transit.domain.model.PathType.BUS;

@Getter
@ToString
public class BusPath extends SubPath{

    private final String busNo; // 버스 번호
    private final int busType; // 버스 타입
    private final String startName; // 출발 지점 이름
    private final Coordinate startCoordinate;
    private final String endName; // 도착 지점 이름
    private final Coordinate endCoordinate;

    public BusPath(
            double distance,
            int sectionTime,
            String busNo,
            int busType,
            String startName,
            Coordinate startCoordinate,
            String endName,
            Coordinate endCoordinate
    ) {
        super(distance, sectionTime);
        this.busNo = busNo;
        this.busType = busType;
        this.startName = startName;
        this.startCoordinate = startCoordinate;
        this.endName = endName;
        this.endCoordinate = endCoordinate;
    }

    @Override
    public PathType getPathType() {
        return BUS;
    }

}
