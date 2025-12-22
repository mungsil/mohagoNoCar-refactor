package com.example.mohago_nocar.transit.infrastructure.distanceDuration;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.DistanceDurationConverter;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.GoogleApiClient;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.GoogleResponseValidator;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.dto.response.GoogleDistanceMatrixResponse;
import com.example.mohago_nocar.transit.domain.model.RouteMetrics;
import com.example.mohago_nocar.transit.infrastructure.error.code.GoogleDistanceMatrixErrorCode;
import com.example.mohago_nocar.transit.infrastructure.error.exception.DistanceMatrixException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleDistanceMatrixApiAdapter implements DistanceDurationApiAdapter {

    private final GoogleApiClient googleApiClient;

    @Override
    public List<RouteMetrics> getDistanceAndDuration(Coordinate origin, List<Coordinate> destinations) {
        GoogleDistanceMatrixResponse response = googleApiClient.getDistanceMatrix(origin, destinations);

        log.info("Distance matrix response: {}", response);
        if (GoogleResponseValidator.hasError(response)) {
            processInvalidResponse(response);
        }

        return processValidResponse(origin, destinations, response);
    }

    @Override
    public CompletableFuture<List<RouteMetrics>> getDistanceAndDurationAsync(Coordinate origin, Set<Coordinate> destinations) {
        CompletableFuture<GoogleDistanceMatrixResponse> distanceMatrixFuture =
                googleApiClient.getDistanceMatrixAsync(origin, destinations);

        return distanceMatrixFuture.thenApply(response -> {
            if (GoogleResponseValidator.hasError(response)) {
                processInvalidResponse(response);
            }

            return processValidResponse(origin, destinations.stream().toList(), response);
        });
    }

    private void processInvalidResponse(GoogleDistanceMatrixResponse response) {
        switch (response.status()) {
            case INVALID_REQUEST, MAX_ELEMENTS_EXCEEDED, MAX_DIMENSIONS_EXCEEDED, REQUEST_DENIED ->
                    throw new DistanceMatrixException(GoogleDistanceMatrixErrorCode.INVALID_REQUEST);
            case OVER_DAILY_LIMIT, OVER_QUERY_LIMIT ->
                    throw new DistanceMatrixException(GoogleDistanceMatrixErrorCode.QUOTA_EXCEEDED);
            default -> throw new DistanceMatrixException(GoogleDistanceMatrixErrorCode.SERVER_ERROR);
        }
    }

    private List<RouteMetrics> processValidResponse(
            Coordinate origin, List<Coordinate> destinations, GoogleDistanceMatrixResponse response) {
        return DistanceDurationConverter.convertMatrixToRouteMetrics(response, origin, destinations);
    }

}
