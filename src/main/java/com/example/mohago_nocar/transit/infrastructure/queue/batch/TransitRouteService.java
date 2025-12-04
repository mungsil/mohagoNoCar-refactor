package com.example.mohago_nocar.transit.infrastructure.queue.batch;

import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpot;
import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;
import com.example.mohago_nocar.transit.infrastructure.route.TransitRouteApiAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class TransitRouteService {

    private final TransitRouteApiAdapter transitRouteApiAdapter;

    public List<? extends CompletableFuture<TransitRoute>> searchTransitRoutesInOrder(List<? extends TravelSpot> travelSpotsInOrder) {
        validateMinSize(travelSpotsInOrder, 2);

        List<CompletableFuture<TransitRoute>> transitRouteApiCallResults = new ArrayList<>();
        int spotNum = travelSpotsInOrder.size();

        for (int i = 0; i < spotNum - 1; i++) {
            Location origin = travelSpotsInOrder.get(i).getLocation();
            Location destination = travelSpotsInOrder.get(i + 1).getLocation();

            CompletableFuture<TransitRoute> apiCalled = transitRouteApiAdapter.getTransitRoute(origin, destination);
            transitRouteApiCallResults.add(apiCalled);
        }

        return transitRouteApiCallResults;
    }

    private void validateMinSize(List<? extends TravelSpot> travelSpotsInOrder, int minSize) {
        if (travelSpotsInOrder == null || travelSpotsInOrder.size() < minSize) {
            throw new IllegalArgumentException("최소 2개 이상의 위치가 필요합니다.");
        }
    }

    private List<CompletableFuture<TransitRoute>>fetchTransitRoutes(List<? extends TravelSpot> travelSpotsInOrder) {
        List<CompletableFuture<TransitRoute>> futures = new ArrayList<>();

        for (int i = 0; i < travelSpotsInOrder.size() - 1; i++) {
            Location origin = travelSpotsInOrder.get(i).getLocation();
            Location destination = travelSpotsInOrder.get(i + 1).getLocation();

            futures.add(transitRouteApiAdapter.getTransitRoute(origin, destination));
        }

        return futures;
    }

}
