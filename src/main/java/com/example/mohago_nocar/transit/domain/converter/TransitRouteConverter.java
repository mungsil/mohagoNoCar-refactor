package com.example.mohago_nocar.transit.domain.converter;

import com.example.mohago_nocar.global.common.domain.vo.Location;
import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.domain.model.*;
import com.example.mohago_nocar.transit.infrastructure.externalApi.odsay.dto.response.OdsayRouteResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
public class TransitRouteConverter {

    private static final int SUBWAY = 1;
    private static final int BUS = 2;
    private static final int WALKING = 3;
    private static final int EARTH_RADIUS = 6371;
    private static final double METER_TO_KILOMETER = 0.001;

    public static TransitRoute convertRouteResponseDtoToTransitRoute(
            OdsayRouteResponse routeResponseDto, Location origin, Location destination) {
        if (routeResponseDto.isTooShortDistance()) {
            return createWalkingRoute(origin, destination);
        }

        JsonNode path = extractPath(routeResponseDto);
        double totalDistance = extractTotalDistance(path);
        int totalTime = extractTotalTime(path);
        List<SubPath> subPaths = extractSubPaths(path);

        return TransitRoute.from(totalTime, totalDistance, subPaths);
    }

    private static TransitRoute createWalkingRoute(Location origin, Location destination) {
        double walkingDistance = getKmDist(origin, destination);
        int walkingTime = (int) Math.round(walkingDistance * 15);
        WalkPath walkPath = new WalkPath(walkingDistance, walkingTime);

        return TransitRoute.from(walkingTime, walkingDistance, List.of(walkPath));
    }


    /**
     * 두 위치(Location) 사이의 거리를 킬로미터 단위로 계산합니다.
     * @param departure
     * @param arrival
     * @return 두 위치(Location) 사이의 거리
     */
    private static Double getKmDist(Location departure, Location arrival) {
        Double dx = Math.abs(departure.getLongitude() - arrival.getLongitude());
        dx = Math.min(dx, 360 - dx);

        Double dy = Math.abs(departure.getLatitude() - arrival.getLatitude());

        Double longitudeDist = convertLongitudeToKmDist(dx, departure.getLatitude());
        Double latitudeDist = convertLatitudeToKmDist(dy);

        return Math.sqrt(longitudeDist * longitudeDist + latitudeDist * latitudeDist);
    }

    private static Double convertLongitudeToKmDist(Double dx, Double stdLatitude) {

        return EARTH_RADIUS * dx * Math.cos(stdLatitude) * Math.PI / 180;
    }

    private static Double convertLatitudeToKmDist(Double dy) {

        return EARTH_RADIUS * dy * Math.PI / 180;
    }

    private static JsonNode extractPath(OdsayRouteResponse routeResponseDto) {
        return routeResponseDto.result().get("path").get(0);
    }

    private static double extractTotalDistance(JsonNode path) {
        JsonNode infoNode = path.get("info");
        double distanceInMeters = infoNode.get("totalDistance").asDouble();

        return distanceInMeters * METER_TO_KILOMETER;
    }

    private static int extractTotalTime(JsonNode path) {
        JsonNode infoNode = path.get("info");

        return infoNode.get("totalTime").asInt();
    }

    private static List<SubPath> extractSubPaths(JsonNode path) {
        JsonNode subPathNode = path.get("subPath");
        return convertPathesNodeToSubPaths(subPathNode);
    }

    private static List<SubPath> convertPathesNodeToSubPaths(JsonNode subPathsNode) {
        return streamJsonNodeOrEmpty(subPathsNode)
                .map(TransitRouteConverter::convertPathNodeToSubPath)
                .toList();
    }

    private static SubPath convertPathNodeToSubPath(JsonNode subPathNode) {
        double distance = (subPathNode.get("distance").asDouble()) * 0.001;
        int sectionTime = subPathNode.get("sectionTime").asInt();
        int trafficType = subPathNode.get("trafficType").asInt();

        return switch (trafficType) {
            case SUBWAY -> createSubwayPath(distance, sectionTime, subPathNode);
            case BUS -> createBusPath(distance, sectionTime, subPathNode);
            case WALKING -> createWalkPath(distance, sectionTime);
            default -> null;
        };
    }

    private static SubPath createSubwayPath(double distance, int sectionTime, JsonNode subPathNode) {
        String startSubwayStationName = subPathNode.get("startName").asText();
        String endSubWayStationName = subPathNode.get("endName").asText();

        double startSubwayStationLongitude = subPathNode.get("startX").asDouble();
        double startSubwayStationLatitude = subPathNode.get("startY").asDouble();

        double endSubwayStationLongitude = subPathNode.get("endX").asDouble();
        double endSubwayStationLatitude = subPathNode.get("endY").asDouble();

        String subwayLineName = subPathNode.get("lane").get(0).get("name").asText();

        return new SubwayPath(
                distance,
                sectionTime,
                subwayLineName,
                startSubwayStationName,
                startSubwayStationLongitude,
                startSubwayStationLatitude,
                endSubWayStationName,
                endSubwayStationLongitude,
                endSubwayStationLatitude
        );
    }

    private static SubPath createBusPath(double distance, int sectionTime, JsonNode subPathNode) {
        String startBusStopName = subPathNode.get("startName").asText();
        String endBusStopName = subPathNode.get("endName").asText();

        double startBusStopLongitude = subPathNode.get("startX").asDouble();
        double startBusStopLatitude = subPathNode.get("startY").asDouble();

        double endBusStopLongitude = subPathNode.get("endX").asDouble();
        double endBusStopLatitude = subPathNode.get("endY").asDouble();

        String busNo = subPathNode.get("lane").get(0).get("busNo").asText();
        int busType = subPathNode.get("lane").get(0).get("type").asInt();

        return new BusPath(
                distance,
                sectionTime,
                busNo,
                busType,
                startBusStopName,
                startBusStopLongitude,
                startBusStopLatitude,
                endBusStopName,
                endBusStopLongitude,
                endBusStopLatitude
        );
    }

    private static SubPath createWalkPath(double distance, int sectionTime) {
        return new WalkPath(distance, sectionTime);
    }

    private static Stream<JsonNode> streamJsonNodeOrEmpty(JsonNode node) {
        if (node == null || !node.isArray()) {
            return Stream.empty();
        }
        return StreamSupport.stream(node.spliterator(), false);
    }

}
