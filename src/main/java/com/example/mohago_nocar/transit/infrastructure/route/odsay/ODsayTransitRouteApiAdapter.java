package com.example.mohago_nocar.transit.infrastructure.route.odsay;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;
import com.example.mohago_nocar.transit.domain.model.WalkPath;
import com.example.mohago_nocar.transit.infrastructure.error.code.OdsayErrorCode;
import com.example.mohago_nocar.transit.infrastructure.error.exception.ODsayRouteException;
import com.example.mohago_nocar.transit.infrastructure.error.exception.ODsayDistanceException;
import com.example.mohago_nocar.transit.infrastructure.route.TransitRouteApiAdapter;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayRouteInvalidResponse;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayRouteValidResponse;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayTransitRouteResponse;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.TransitRouteConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ODsayTransitRouteApiAdapter implements TransitRouteApiAdapter {

    private static final int EARTH_RADIUS = 6371;

    private final ODsayApiRateLimitedClient rateLimitedClient;

    @Override
    public TransitRoute getTransitRouteBetweenLocations(Location origin, Location destination) {
        ODsayTransitRouteResponse response = rateLimitedClient.searchTransitRoute(origin.getCoordinate(), destination.getCoordinate());
        if (!response.isValid()) {
            try {
                processInvalidResponse((ODsayRouteInvalidResponse)response);
            } catch (ODsayDistanceException e) {
                return createShortDistanceResponse(origin, destination);
            }
        }

        return processValidResponse(origin, destination, response);
    }

    @Override
    public CompletableFuture<TransitRoute> getTransitRouteWithThrottling(Location origin, Location destination) {
        CompletableFuture<ODsayTransitRouteResponse> future =
                rateLimitedClient.searchTransitRouteAsync(origin.getCoordinate(), destination.getCoordinate());

        return future.thenApply(response -> {
            if (!response.isValid()) {
                try {
                    processInvalidResponse((ODsayRouteInvalidResponse)response);
                } catch (ODsayDistanceException e) {
                    return createShortDistanceResponse(origin, destination);
                }
            }

            return processValidResponse(origin, destination, response);
        });
    }

    private void processInvalidResponse(ODsayRouteInvalidResponse response) {
        log.warn("ODsay Invalid response: {}", response);

        OdsayErrorCode errorCode = OdsayErrorCode.from(response.getErrorCode());
        if (errorCode.isDistanceError()) {
            throw new ODsayDistanceException(errorCode);
        }

        throw new ODsayRouteException(errorCode);
    }

    private TransitRoute processValidResponse(Location origin, Location destination, ODsayTransitRouteResponse response) {
        log.info("Odsay API response: {}", response);
        return TransitRouteConverter.convertToTransitRoute((ODsayRouteValidResponse) response, origin, destination);
    }

    private TransitRoute createShortDistanceResponse(Location origin, Location destination) {
        double walkingDistance = getKmDist(origin.getCoordinate(), destination.getCoordinate());
        int walkingTime = (int) Math.round(walkingDistance * 15);
        WalkPath walkPath = new WalkPath(walkingDistance, walkingTime);

        return TransitRoute.from(origin, destination, walkingTime, walkingDistance, List.of(walkPath));
    }


    /**
     * 두 위치(Location) 사이의 거리를 킬로미터 단위로 계산합니다.
     * @param departure
     * @param arrival
     * @return 두 위치(Location) 사이의 거리
     */
    private Double getKmDist(Coordinate departure, Coordinate arrival) {
        Double dx = Math.abs(departure.getLongitude() - arrival.getLongitude());
        dx = Math.min(dx, 360 - dx);

        Double dy = Math.abs(departure.getLatitude() - arrival.getLatitude());

        Double longitudeDist = convertLongitudeToKmDist(dx, departure.getLatitude());
        Double latitudeDist = convertLatitudeToKmDist(dy);

        return Math.sqrt(longitudeDist * longitudeDist + latitudeDist * latitudeDist);
    }

    private Double convertLongitudeToKmDist(Double dx, Double stdLatitude) {
        return EARTH_RADIUS * dx * Math.cos(Math.toRadians(stdLatitude)) * Math.PI / 180;
    }

    private Double convertLatitudeToKmDist(Double dy) {
        return EARTH_RADIUS * dy * Math.PI / 180;
    }

}
