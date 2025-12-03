package com.example.mohago_nocar.transit.infrastructure.route.odsay;

import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;
import com.example.mohago_nocar.transit.infrastructure.route.TransitRouteApiAdapter;
import com.example.mohago_nocar.transit.infrastructure.route.TransitRouteApiExecutor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Deprecated
@Component
@Slf4j
@RequiredArgsConstructor
public class ODsayTransitRouteApiExecutor implements TransitRouteApiExecutor {

    private final TransitRouteApiAdapter transitRouteApiAdapter;
    private ExecutorService singleThreadExecutor;

    @PostConstruct
    private void init() {
        this.singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public CompletableFuture<List<TransitRoute>> execute(final List<Location> locations) {
        return CompletableFuture.supplyAsync(() -> {
                    validateLocations(locations);
                    return fetchTransitRoutes(locations);
                }, singleThreadExecutor);
    }

    private void validateLocations(List<Location> locations) {
        if (locations == null || locations.size() < 2) {
            throw new IllegalArgumentException("최소 2개 이상의 위치가 필요합니다.");
        }
    }

    private List<TransitRoute> fetchTransitRoutes(List<Location> locations) {
        List<TransitRoute> routes = new ArrayList<>();

        for (int i = 0; i < locations.size() - 1; i++) {
            Location origin = locations.get(i);
            Location destination = locations.get(i + 1);

            TransitRoute route = transitRouteApiAdapter.getTransitRouteBetweenLocations(origin, destination);
            routes.add(route);
        }

        return routes;
    }

    @PreDestroy
    private void shutdown() {
        if (singleThreadExecutor == null) {
            return;
        }

        log.info("Single thread executor shutdown started");
        singleThreadExecutor.shutdown();
        try {
            if (!singleThreadExecutor.awaitTermination(20, TimeUnit.SECONDS)) {
                log.warn("정상 종료에 실패했습니다. 강제 종료를 시작합니다.");
                singleThreadExecutor.shutdownNow();
                if (!singleThreadExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.error("강제 종료에 실패하였습니다.");
                }
            }
        } catch (InterruptedException e) {
            log.error("인터럽트 발생", e);
            singleThreadExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
