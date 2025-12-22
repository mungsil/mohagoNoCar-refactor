package com.example.mohago_nocar.transit.applicatoin;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.domain.model.RouteMetrics;
import com.example.mohago_nocar.transit.domain.service.TransitRouteSummaryUseCase;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.DistanceDurationApiAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TransitRouteSummaryUseCaseImpl implements TransitRouteSummaryUseCase {

    private final DistanceDurationApiAdapter apiAdapter;

    @Override
    public CompletableFuture<List<RouteMetrics>> getRouteSummary(Coordinate origin, Set<Coordinate> destinations) {
        for (Coordinate destination : destinations) {
            if (origin.equals(destination)) {
                throw new IllegalArgumentException("Origin and destination are equal");
            }
        }

        return apiAdapter.getDistanceAndDurationAsync(origin, destinations);
    }

}
