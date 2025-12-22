package com.example.mohago_nocar.course.application.spot;

import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpot;
import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpotFestival;
import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpotPlace;
import com.example.mohago_nocar.course.domain.repository.TravelSpotRepository;
import com.example.mohago_nocar.course.domain.service.TravelSpotUseCase;
import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.festival.domain.service.FestivalUseCase;
import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.global.common.exception.CustomException;
import com.example.mohago_nocar.global.common.exception.GlobalStatus;
import com.example.mohago_nocar.global.common.exception.InvalidValueException;
import com.example.mohago_nocar.place.domain.model.Place;
import com.example.mohago_nocar.place.domain.service.PlaceUseCase;
import com.example.mohago_nocar.plan.application.v1.strategy.RouteOptimizationStrategy;
import com.example.mohago_nocar.transit.domain.model.RouteMetrics;
import com.example.mohago_nocar.transit.domain.service.TransitRouteSummaryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import static com.example.mohago_nocar.plan.presentation.exception.PlanErrorCode.TRAVEL_DATE_NOT_IN_FESTIVAL_PERIOD;

@Service
@RequiredArgsConstructor
@Slf4j
public class TravelSpotService implements TravelSpotUseCase {

    private final TravelSpotRepository travelSpotRepository;
    private final TransitRouteSummaryUseCase transitRouteSummaryUseCase;
    private final RouteOptimizationStrategy routeOptimizationStrategy;
    private final FestivalUseCase festivalUseCase;
    private final PlaceUseCase placeUseCase;

    @Override
    public Set<TravelSpot> determineOptimizedTravelOrder(Set<TravelSpot> unorderedSpots) {
        if (unorderedSpots == null || unorderedSpots.isEmpty()) {
            return Collections.emptySet();
        }

        // 중복 좌표를 갖는 장소 존재 가능 -> 임시 중복 좌표 제거
        Map<Coordinate, Set<TravelSpot>> unorderedSpotsByCoordinate = mapByCoordinateFrom(unorderedSpots);
        Set<Coordinate> uniqueCoordinates = unorderedSpotsByCoordinate.keySet();

        // 고유한 좌표들을 방문할 순서 결정
        List<RouteMetrics> routeMetrics = fetchRouteMetrics(uniqueCoordinates);
        List<? extends Coordinate> determinedOrder = routeOptimizationStrategy.calculateOptimalRoute(
                uniqueCoordinates.stream().toList(), routeMetrics);

        // 중복된 좌표를 가졌던 장소를 포함한 모든 장소에게 방문 순서 부여
        return assignOrderToSpot(determinedOrder, unorderedSpotsByCoordinate);
    }

    private Map<Coordinate, Set<TravelSpot>> mapByCoordinateFrom(Set<TravelSpot> travelSpots) {
        return travelSpots.stream()
                .collect(Collectors.groupingBy(
                        travelSpot -> travelSpot.getLocation().getCoordinate(),
                        Collectors.toCollection(HashSet::new)
                ));
    }

    /**
     * 모든 좌표 간의 이동 요약 정보(거리(km), 이동 시간(minutes))을 구합니다.
     *
     * @param coordinates 거리, 이동 시간을 구하는 대상 좌표
     * @return 좌표 간의 거리 및 이동시간 <p>예시: A, B 좌표가 주어지면 A->B, B->A 간의 거리 및 이동 시간을 구합니다. </p>
     */
    private List<RouteMetrics> fetchRouteMetrics(Set<Coordinate> coordinates) {
        List<CompletableFuture<List<RouteMetrics>>> futureRouteMetricsNested = new ArrayList<>();

        for (Coordinate origin : coordinates) {
            Set<Coordinate> destinations = new HashSet<>(coordinates);
            destinations.remove(origin);

            futureRouteMetricsNested.add(
                    transitRouteSummaryUseCase.getRouteSummary(origin, destinations));
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                futureRouteMetricsNested.toArray(new CompletableFuture[0]))
                .orTimeout(8, TimeUnit.SECONDS);

        try {
            allOf.join();
        } catch (Exception e) {
            log.error("이동 경로 요약 조회 실패", e);
            throw new RuntimeException(e);
        }

        return futureRouteMetricsNested.stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .toList();
    }

    private Set<TravelSpot> assignOrderToSpot(
            List<? extends Coordinate> visitOrder, Map<Coordinate, Set<TravelSpot>> spotsByCoordinate
    ) {
        if (visitOrder.size() != spotsByCoordinate.keySet().size()) { // todo map.size 동작
            log.error("경위도 개수가 일치하지 않습니다. visitOrder- {}개, spotsByCoordinate- {}개", visitOrder.size(), spotsByCoordinate.size());
            throw new CustomException(GlobalStatus.INTERNAL_SERVER_ERROR);
        }

        int order = 0;
        Set<TravelSpot> orderedSpot = new HashSet<>();
        for (Coordinate nxt : visitOrder) {
            Set<TravelSpot> nxtSpots = spotsByCoordinate.get(nxt);
            for (TravelSpot spot : nxtSpots) {
                spot.setOrder(order);
                order = order + 1;
            }
            orderedSpot.addAll(nxtSpots);
        }

        return orderedSpot;
    }

    @Override
    public List<TravelSpot> saveAll(Set<TravelSpot> spotsWithOrder) {
        return travelSpotRepository.saveAll(spotsWithOrder);
    }

    @Override
    public List<TravelSpot> getByCourseId(Long travelCourseId) {
        return travelSpotRepository.findByTravelCourseId(travelCourseId);
    }

    @Override
    public Set<TravelSpot> makeSpotsWithoutOrder(TravelCourse ownerCourse, Long festivalId, LocalDate travelDate, List<String> placeIds) {
        Festival festival = getFestivalOrThrow(festivalId, travelDate);
        List<Place> places = getNearPlacesOfFestival(festival, placeIds);

        return createUnOrderedTravelSpots(ownerCourse, festival, places);
    }

    private Festival getFestivalOrThrow(Long festivalId, LocalDate travelDate) {
        var festival = festivalUseCase.getFestival(festivalId);
/*        if (!festival.isOpen(travelDate)) {
            throw new InvalidValueException(TRAVEL_DATE_NOT_IN_FESTIVAL_PERIOD);
        }*/
        return festival;
    }

    private List<Place> getNearPlacesOfFestival(Festival festival, List<String> placeIds) {
        List<Place> places = placeUseCase.getFestivalNearPlacesById(festival.getId(), placeIds);
        log.info("카카오로부터 조회해온 장소:{}", places);
        // todo refactoring : 캐싱 정책 (all or nothing)을 담는 객체 생성
        if (places.isEmpty()) {
            log.info("Place cache miss가 발생했습니다. 원본 데이터를 요청하여 재캐싱합니다.");
            places = placeUseCase.cachePlaces(festival.getId(), festival.getCoordinate()).stream()
                    .filter(place -> placeIds.contains(place.getKakaoId()))
                    .toList();
        }
        return places;
    }

    @Override
    public Set<TravelSpot> createUnOrderedTravelSpots(TravelCourse course, Festival festival, List<Place> places) {
        log.info("인자로 전달된 place 사이즈: {}", places.size());
        log.info("places:{}", places);
        Set<TravelSpot> travelSpots = new HashSet<>();
        travelSpots.add(TravelSpotFestival.createWithNoOrder(course, festival));
        for (Place place : places) {
            travelSpots.add(TravelSpotPlace.createWithNoOrder(course, place));
        }
        log.info("여행 장소: {}", travelSpots);

        return travelSpots;
    }

}
