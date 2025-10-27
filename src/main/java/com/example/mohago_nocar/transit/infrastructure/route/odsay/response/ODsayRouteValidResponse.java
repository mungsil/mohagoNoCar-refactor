package com.example.mohago_nocar.transit.infrastructure.route.odsay.response;

import lombok.*;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ToString
public class ODsayRouteValidResponse extends ODsayTransitRouteResponse {

    private final int totalTime;
    private final double totalDistance;

    @Getter
    @RequiredArgsConstructor
    @Builder
    @ToString
    public static class SubPath {

        private final int trafficType;
        private final double distanceMeter;
        private final int sectionTimeMin;

        // common fields of types bus, subway
        private final String startName;
        private final Double startLongitude;
        private final Double startLatitude;
        private final String endName;
        private final Double endLongitude;
        private final Double endLatitude;

        // fields for bus
        private final String busNo;
        private final Integer busType;

        // fields for subway
        private final String subwayLineName;

    }

    private final List<SubPath> subPaths;

    @Override
    public Boolean isValid() {
        return true;
    }

}
