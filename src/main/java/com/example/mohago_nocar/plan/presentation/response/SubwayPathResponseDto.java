package com.example.mohago_nocar.plan.presentation.response;

import com.example.mohago_nocar.transit.domain.model.SubPath;
import com.example.mohago_nocar.transit.domain.model.SubwayPath;
import lombok.Builder;
import lombok.Getter;

import static com.example.mohago_nocar.transit.domain.model.PathType.SUBWAY;

@Getter
public class SubwayPathResponseDto extends SubPathResponseDto{

    private final String subwayLineName;
    private final String startPlaceName;
    private final double startLongitude;
    private final double startLatitude;
    private final String endPlaceName;
    private final double endLongitude;
    private final double endLatitude;

    public static SubwayPathResponseDto of(SubPath subPath) {
        SubwayPath subwayPath = (SubwayPath) subPath;

        return SubwayPathResponseDto.builder()
                .distance(subwayPath.getDistanceKm())
                .sectionTime(subwayPath.getSectionTimeMin())
                .subwayLineName(subwayPath.getSubwayLineName())
                .startPlaceName(subwayPath.getStartName())
                .startLongitude(subwayPath.getStartCoordinate().getLongitude())
                .startLatitude(subwayPath.getStartCoordinate().getLatitude())
                .endPlaceName(subwayPath.getEndName())
                .endLongitude(subwayPath.getEndCoordinate().getLongitude())
                .endLatitude(subwayPath.getEndCoordinate().getLatitude())
                .build();
    }

    @Builder
    private SubwayPathResponseDto(
            double distance,
            int sectionTime,
            String subwayLineName,
            String startPlaceName,
            double startLongitude,
            double startLatitude,
            String endPlaceName,
            double endLongitude,
            double endLatitude
    ) {
        super(distance, sectionTime, SUBWAY);
        this.subwayLineName = subwayLineName;

        this.startPlaceName = startPlaceName;
        this.startLongitude = startLongitude;
        this.startLatitude = startLatitude;

        this.endPlaceName = endPlaceName;
        this.endLongitude = endLongitude;
        this.endLatitude = endLatitude;
    }
}
