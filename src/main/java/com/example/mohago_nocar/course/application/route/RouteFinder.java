package com.example.mohago_nocar.course.application.route;

import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpot;
import com.example.mohago_nocar.transit.infrastructure.route.TransitRouteApiAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
@RequiredArgsConstructor
public class RouteFinder {

    private final TransitRouteApiAdapter transitRouteApiAdapter;

    private ReentrantLock lock = new ReentrantLock(true);

    public List<CompletableFuture<RouteStep>> findRouteWithThrottling(List<TravelSpot> travelSpots) {
        List<CompletableFuture<RouteStep>> steps = new ArrayList<>();

        for (int i = 0; i < travelSpots.size()-1; i++) {
            TravelSpot originSpot = travelSpots.get(i);
            TravelSpot destinationSpot = travelSpots.get(i + 1);

            CompletableFuture<RouteStep> routeFuture =
                    transitRouteApiAdapter.getTransitRouteWithThrottling(originSpot.getLocation(), destinationSpot.getLocation())
                            .thenApply(transitRoute -> RouteStep.from(originSpot, destinationSpot, transitRoute));

            steps.add(routeFuture);
        }

        return steps;
    }

    /**
     * 여행 코스 내 존재하는 장소 사이의 이동 경로를 찾습니다.
     * @param travelSpots 같은 여행 코스 내 방문 순서가 정해진 여행 장소들
     * @return 출발 장소, 도착 장소 간의 이동 경로들
     * @implNote 이동 경로 조회 API가 허용하는 초당 호출량은 최대 5회입니다.
     * 따라서 병렬적으로 여러 여행 코스 내의 이동 경로를 구할 경우, 전체적인 처리 시간이 증가합니다.
     * 이를 방지하기 위해 한 여행 코스 내의 이동 경로 조회 API를 호출한 후에 다음 여행 코스의 이동 경로를 구하도록 제한합니다.
     */
    public List<CompletableFuture<RouteStep>> findRouteInTravelCourse(Long travelCourseId, List<TravelSpot> travelSpots) {
        validateMinSize(travelSpots, 2);
        validateSpotBelongsToSameCourse(travelCourseId, travelSpots);
        sortByVisitOrder(travelSpots);

        List<CompletableFuture<RouteStep>> steps = new ArrayList<>();

        try{
            lock.lock();
            log.info("get Lock in findRouteInTravelCourse");

            for (int i = 0; i < travelSpots.size()-1; i++) {
                TravelSpot originSpot = travelSpots.get(i);
                TravelSpot destinationSpot = travelSpots.get(i + 1);

                CompletableFuture<RouteStep> routeFuture =
                        transitRouteApiAdapter.getTransitRouteWithThrottling(originSpot.getLocation(), destinationSpot.getLocation())
                                .thenApply(transitRoute -> RouteStep.from(originSpot, destinationSpot, transitRoute));

                steps.add(routeFuture);
            }

        } finally {
            lock.unlock();
            log.info("release Lock in findRouteInTravelCourse");
        }

        return steps;
    }

    private void validateSpotBelongsToSameCourse(Long travelCourseId, List<TravelSpot> travelSpots) {
        for (TravelSpot travelSpot : travelSpots) {
            if (!Objects.equals(travelSpot.getCourseId(), travelCourseId)) {
                log.error("여행 장소가 속한 코스의 아이디가 {}입니다. 그러나 여행 코스 아이디는 {} 이어야 합니다.",
                        travelSpot.getCourseId(), travelCourseId);
                throw new RuntimeException("여행 장소들이 모두 같은 여행 코스에 소속되지 않았습니다.");
            }
        }
    }

    private void sortByVisitOrder(List<TravelSpot> travelSpots) {
        Collections.sort(travelSpots);
    }

    private void validateMinSize(List<? extends TravelSpot> travelSpotsInOrder, int minSize) {
        if (travelSpotsInOrder == null || travelSpotsInOrder.size() < minSize) {
            System.out.println("travelSpotInOrder: " + travelSpotsInOrder);
            throw new IllegalArgumentException("최소 2개 이상의 위치가 필요합니다.");
        }
    }

}
