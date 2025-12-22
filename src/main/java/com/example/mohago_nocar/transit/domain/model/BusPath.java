package com.example.mohago_nocar.transit.domain.model;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.example.mohago_nocar.transit.domain.model.PathType.BUS;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString(callSuper = true)
public class BusPath extends SubPath{

    private String busNo; // 버스 번호

    private int busType; // 버스 타입

    private String startName; // 출발 지점 이름

    private Coordinate startCoordinate;

    private String endName; // 도착 지점 이름

    private Coordinate endCoordinate;

    public BusPath(
            double distanceKm,
            int timeTakenMin,
            String busNo,
            int busType,
            String startName,
            Coordinate startCoordinate,
            String endName,
            Coordinate endCoordinate
    ) {
        super(distanceKm, timeTakenMin, BUS);
        this.busNo = busNo;
        this.busType = busType;
        this.startName = startName;
        this.startCoordinate = startCoordinate;
        this.endName = endName;
        this.endCoordinate = endCoordinate;
    }

//    @Override
//    public PathType getPathType() {
//        return getPathType();
//    }

}
