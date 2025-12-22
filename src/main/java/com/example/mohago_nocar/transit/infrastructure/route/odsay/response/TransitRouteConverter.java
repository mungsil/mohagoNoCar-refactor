package com.example.mohago_nocar.transit.infrastructure.route.odsay.response;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.domain.model.*;

import java.util.List;

public class TransitRouteConverter {

    private static final double METER_TO_KILOMETER = 0.001;
    private static final int SUBWAY = 1;
    private static final int BUS = 2;
    private static final int WALKING = 3;

    public static TransitRoute convertToTransitRoute(ODsayRouteValidResponse response, Location origin, Location destination) {
        List<SubPath> subPaths = response.getSubPaths().stream()
                .map(path ->
                        switch (path.getTrafficType()) {
                            case SUBWAY -> createSubWay(path);
                            case BUS -> createBus(path);
                            case WALKING -> createWalking(path);
                            default -> throw new IllegalStateException("Unexpected value: " + path.getTrafficType());
                        }
                ).toList();

        return TransitRoute.from(origin, destination, response.getTotalTime(), response.getTotalDistance(), subPaths);
    }

    private static SubPath createSubWay(ODsayRouteValidResponse.SubPath path) {
        return new SubwayPath(
                path.getDistanceMeter() * METER_TO_KILOMETER,
                path.getSectionTimeMin(),
                path.getSubwayLineName(),
                path.getStartName(),
                Coordinate.from(path.getStartLongitude(), path.getStartLatitude()),
                path.getEndName(),
                Coordinate.from(path.getEndLongitude(), path.getEndLatitude())
        );
    }

    private static SubPath createBus(ODsayRouteValidResponse.SubPath path) {
        return new BusPath(
                path.getDistanceMeter() * METER_TO_KILOMETER,
                path.getSectionTimeMin(),
                path.getBusNo(),
                path.getBusType(),
                path.getStartName(),
                Coordinate.from(path.getStartLongitude(), path.getStartLatitude()),
                path.getEndName(),
                Coordinate.from(path.getEndLongitude(), path.getEndLatitude())
        );
    }

    private static SubPath createWalking(ODsayRouteValidResponse.SubPath path) {
        return new WalkPath(path.getDistanceMeter() * METER_TO_KILOMETER, path.getSectionTimeMin());
    }

}
