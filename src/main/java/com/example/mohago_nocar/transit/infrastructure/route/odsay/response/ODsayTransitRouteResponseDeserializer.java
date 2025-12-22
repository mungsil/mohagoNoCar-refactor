package com.example.mohago_nocar.transit.infrastructure.route.odsay.response;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@JsonComponent
@Slf4j
public class ODsayTransitRouteResponseDeserializer extends JsonDeserializer<ODsayTransitRouteResponse> {

    private static final int SUBWAY = 1;
    private static final int BUS = 2;
    private static final int WALKING = 3;

    @Override
    public ODsayTransitRouteResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode responseJson = parseJsonResponse(jsonParser);

        return isInvalidResponse(responseJson) ?
                createInvalidResponse(responseJson) :
                createValidResponse(responseJson);
    }

    private ODsayTransitRouteResponse createInvalidResponse(JsonNode responseJson) {
        log.info(responseJson.asText());
        var errorInfoJson = find(responseJson, "error").get();
        var errorCode = getFields(errorInfoJson, "code");
        var errorMessage = getFields(errorInfoJson, "message", "msg");

        return new ODsayRouteInvalidResponse(errorCode, errorMessage);
    }

    private ODsayTransitRouteResponse createValidResponse(JsonNode responseJson) {
        var pathJson = responseJson.get("result").get("path").get(0);
        var infoJson = pathJson.get("info");

        var totalDistance = infoJson.get("totalDistance").asDouble();
        var totalTime = infoJson.get("totalTime").asInt();

        var subPathsJson = pathJson.get("subPath");
        var subPaths = streamJsonNodeOrEmpty(subPathsJson)
                .map(this::parseByPathType)
                .toList();

        return new ODsayRouteValidResponse(totalTime, totalDistance, subPaths);
    }

    private ODsayRouteValidResponse.SubPath parseByPathType(JsonNode pathJson) {
        var distance = (pathJson.get("distance").asDouble());
        var sectionTime = pathJson.get("sectionTime").asInt();
        var trafficType = pathJson.get("trafficType").asInt();

        return switch (trafficType) {
            case SUBWAY -> createSubwayPath(distance, sectionTime, pathJson);
            case BUS -> createBusPath(distance, sectionTime, pathJson);
            case WALKING -> createWalkPath(distance, sectionTime);
            default -> throw new IllegalStateException("Unexpected traffic type: " + trafficType);
        };
    }

    private ODsayRouteValidResponse.SubPath createSubwayPath(double distance, int sectionTime, JsonNode pathJson) {
        String startSubwayStationName = pathJson.get("startName").asText();
        double startSubwayStationLongitude = pathJson.get("startX").asDouble();
        double startSubwayStationLatitude = pathJson.get("startY").asDouble();

        String endSubWayStationName = pathJson.get("endName").asText();
        double endSubwayStationLongitude = pathJson.get("endX").asDouble();
        double endSubwayStationLatitude = pathJson.get("endY").asDouble();

        String subwayLineName = pathJson.get("lane").get(0).get("name").asText();

        return ODsayRouteValidResponse.SubPath.builder()
                .trafficType(SUBWAY)
                .distanceMeter(distance)
                .sectionTimeMin(sectionTime)
                .startName(startSubwayStationName)
                .startLongitude(startSubwayStationLongitude)
                .startLatitude(startSubwayStationLatitude)
                .endName(endSubWayStationName)
                .endLongitude(endSubwayStationLongitude)
                .endLatitude(endSubwayStationLatitude)
                .subwayLineName(subwayLineName)
                .build();
    }

    private ODsayRouteValidResponse.SubPath createBusPath(double distance, int sectionTime, JsonNode pathJson) {
        String startBusStopName = pathJson.get("startName").asText();
        double startBusStopLongitude = pathJson.get("startX").asDouble();
        double startBusStopLatitude = pathJson.get("startY").asDouble();

        String endBusStopName = pathJson.get("endName").asText();
        double endBusStopLongitude = pathJson.get("endX").asDouble();
        double endBusStopLatitude = pathJson.get("endY").asDouble();

        String busNo = pathJson.get("lane").get(0).get("busNo").asText();
        int busType = pathJson.get("lane").get(0).get("type").asInt();

        return ODsayRouteValidResponse.SubPath.builder()
                .trafficType(BUS)
                .distanceMeter(distance)
                .sectionTimeMin(sectionTime)
                .startName(startBusStopName)
                .startLongitude(startBusStopLongitude)
                .startLatitude(startBusStopLatitude)
                .endName(endBusStopName)
                .endLongitude(endBusStopLongitude)
                .endLatitude(endBusStopLatitude)
                .busNo(busNo)
                .busType(busType)
                .build();
    }

    private ODsayRouteValidResponse.SubPath createWalkPath(double distance, int sectionTime) {
        return ODsayRouteValidResponse.SubPath.builder()
                .trafficType(WALKING)
                .distanceMeter(distance)
                .sectionTimeMin(sectionTime)
                .build();
    }

    private Stream<JsonNode> streamJsonNodeOrEmpty(JsonNode node) {
        if (node == null || !node.isArray()) {
            return Stream.empty();
        }
        return StreamSupport.stream(node.spliterator(), false);
    }

    private boolean isInvalidResponse(JsonNode responseJson) {
        return find(responseJson, "error").isPresent();
    }

    private JsonNode parseJsonResponse(JsonParser jsonParser) throws IOException {
        return jsonParser.getCodec().readTree(jsonParser);
    }

    private Optional<JsonNode> find(JsonNode node, String fieldName) {
        try {
            JsonNode result = node.get(fieldName);
            return Optional.of(result);
        } catch (NullPointerException exception) {
            return Optional.empty();
        }
    }

    private String getFields(JsonNode jsonNode, String... fields) {
        return Arrays.stream(fields)
                .map(jsonNode::findPath)
                .filter(node -> !node.isMissingNode())
                .findFirst()
                .map(JsonNode::asText)
                .orElse(null);
    }

}
