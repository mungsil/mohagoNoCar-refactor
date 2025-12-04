package com.example.mohago_nocar.plan.application.v2;

import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.festival.domain.service.FestivalUseCase;
import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.global.common.exception.CustomException;
import com.example.mohago_nocar.global.common.exception.GlobalStatus;
import com.example.mohago_nocar.global.common.exception.InvalidValueException;
import com.example.mohago_nocar.plan.application.v2.response.BatchInfoDto;
import com.example.mohago_nocar.plan.application.v2.response.GetTravelCoursePlanResponseDto;
import com.example.mohago_nocar.plan.application.v2.response.PlanTravelCourseResponseDtoV2;
import com.example.mohago_nocar.plan.domain.service.PlanTravelCourseUsecaseRequestDto;
import com.example.mohago_nocar.plan.infrastructure.queue.PlanEvent;
import com.example.mohago_nocar.plan.infrastructure.queue.producer.PlanEventProducer;
import com.example.mohago_nocar.transit.infrastructure.queue.batch.TransitRouteBatchLauncher;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import com.example.mohago_nocar.user.domain.UserUseCase;
import com.example.mohago_nocar.place.domain.model.Place;
import com.example.mohago_nocar.place.domain.service.PlaceUseCase;
import com.example.mohago_nocar.plan.application.v1.response.TravelRouteResponseDto;
import com.example.mohago_nocar.plan.application.v1.strategy.RouteOptimizationStrategy;
import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.plan.domain.model.TravelCourseInPlan;
import com.example.mohago_nocar.plan.domain.repository.TravelCoursePlanRepository;
import com.example.mohago_nocar.plan.domain.service.TravelCoursePlanUseCaseV2;
import com.example.mohago_nocar.plan.presentation.exception.PlanErrorCode;
import com.example.mohago_nocar.plan.presentation.v2.GetTravelPlanRequestDto;
import com.example.mohago_nocar.plan.presentation.v2.AsyncPlanTravelCourseRequestDto;
import com.example.mohago_nocar.transit.domain.model.RouteMetrics;
import com.example.mohago_nocar.transit.infrastructure.queue.batch.TransitRouteBatchExecution;
import com.example.mohago_nocar.transit.infrastructure.queue.batch.TransitRouteBatchUseCase;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.DistanceDurationApiAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.mohago_nocar.plan.presentation.exception.PlanErrorCode.TRAVEL_DATE_NOT_IN_FESTIVAL_PERIOD;

@Service
@Slf4j
@RequiredArgsConstructor
public class TravelCoursePlanServiceV2 implements TravelCoursePlanUseCaseV2 {

    private final PlaceUseCase placeUseCase;
    private final FestivalUseCase festivalUseCase;
    private final TransitRouteBatchUseCase transitRouteBatchUseCase;
    private final UserUseCase userUseCase;

    private final ExecutorService virtualThreadExecutor;
    private final DistanceDurationApiAdapter distanceDurationApiAdapter;
    private final RouteOptimizationStrategy routeOptimizationStrategy;
    private final PlanEventProducer planEventProducer;

    private final TransitRouteBatchLauncher transitRouteBatchLauncher;
    private final TravelCoursePlanRepository travelCoursePlanRepository;

    @Override
    public PlanTravelCourseResponseDtoV2 doAsyncPlan(AsyncPlanTravelCourseRequestDto request) {
        // 플랜 객체 생성
        AnonymousUser user = userUseCase.getOrCreate(request.fcmToken());
        TravelCourseInPlan coursePlan = TravelCourseInPlan.create(user);
        TravelCourseInPlan plan = travelCoursePlanRepository.save(coursePlan);

        // 플랜 이벤트 큐에 발행
        PlanEvent planEvent = PlanEvent.of(plan.getId(), user.getId(), request.festivalId(), request.placeIds(), request.travelDate());
        planEventProducer.produce(planEvent);

        // 반환
        return PlanTravelCourseResponseDtoV2.builder()
                .userId(user.getId().toString())
                .planId(plan.getId()).build();
    }

    @Override
    public void doPlan(PlanTravelCourseUsecaseRequestDto request) {
        Festival festival = validateAndGetFestival(request.festivalId(), request.travelDate());
        List<Place> attractions = getAttractions(festival, request.placeIds());

        Map<Coordinate, List<String>> namesByCoordinate = mergeFestivalAndAttractionName(festival, attractions);

        List<Coordinate> optimalRouteCoordinates = findOptimalRoute(namesByCoordinate);
        List<Location> optimalRouteLocations = mapCoordinatesToLocations(namesByCoordinate, optimalRouteCoordinates);

        TransitRouteBatchExecution batchExecution = transitRouteBatchUseCase.createAndSaveExecution(
                 optimalRouteLocations.size(), request.userId(), request.planId());

        transitRouteBatchLauncher.launch(batchExecution, optimalRouteLocations);
    }

    @Override
    public GetTravelCoursePlanResponseDto get(GetTravelPlanRequestDto request) {
        // fetch plan from database
        Optional<TravelCourseInPlan> optionalPlan = travelCoursePlanRepository.findById(request.planId());
        TravelCourseInPlan plan = optionalPlan.orElseThrow(() -> new CustomException(GlobalStatus.ENTITY_NOT_FOUND));

        // dto mapping
        List<TravelRouteResponseDto> routes = plan.getTransitRoutes().stream()
                .map(TravelRouteResponseDto::of) // todo dto 이름 변경
                .toList();

        return GetTravelCoursePlanResponseDto.of(routes);
    }


    public BatchInfoDto getBatchInfo(String batchId) {
        TransitRouteBatchExecution batchExecution = transitRouteBatchUseCase.getById(batchId);
        if (batchExecution == null) throw new CustomException(PlanErrorCode.BATCH_TASK_NOT_FOUND);
        return BatchInfoDto.from(batchExecution);
    }

    private List<Location> mapCoordinatesToLocations(
            Map<Coordinate, List<String>> namesByCoordinate,
            List<Coordinate> coordinates
    ) {
        return coordinates.stream()
                .flatMap(coordinate -> {
                    List<String> names = namesByCoordinate.get(coordinate);
                    return names.stream()
                            .map(name -> Location.of(name, coordinate));
                }).toList();
    }

    private List<Coordinate> collectCoordinate(Map<Coordinate, List<String>> namesByCoordinate) {
        return namesByCoordinate.keySet().stream().toList();
    }

    private List<Coordinate> createDestination(List<Coordinate> coordinates, int excludeIndex) {
        return IntStream.range(0, coordinates.size())
                .filter(index -> index != excludeIndex)
                .mapToObj(coordinates::get)
                .toList();
    }

    private Future<List<RouteMetrics>> distanceDurationApiCall(List<Coordinate> coordinates, int index) {
        return virtualThreadExecutor.submit(() -> {
            Coordinate origin = coordinates.get(index);
            List<Coordinate> destinations = createDestination(coordinates, index);

            return distanceDurationApiAdapter.getDistanceAndDuration(origin, destinations);
        });
    }

    /**
     * 좌표 간의 거리(km), 이동 시간(minutes)를 가져오는 외부 API를 호출하여 응답을 생성합니다.
     * @param coordinates 거리, 이동 시간을 구하는 대상 좌표
     * @return 좌표 간의 거리 및 이동시간
     */
    private List<RouteMetrics> fetchDistanceAndDurations(List<Coordinate> coordinates) {
        var futures = IntStream.range(0, coordinates.size())
                .mapToObj(index -> distanceDurationApiCall(coordinates, index))
                .toList();

        return futures.stream()
                .map(this::awaitFutureResult)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<Coordinate> findOptimalRoute(Map<Coordinate, List<String>> namesByCoordinate) {
        var coordinates = collectCoordinate(namesByCoordinate);
        var routeMetrics = fetchDistanceAndDurations(coordinates);

        return routeOptimizationStrategy.calculateOptimalRoute(coordinates, routeMetrics);
    }


    private Festival validateAndGetFestival(Long festivalId, LocalDate travelDate) {
        var festival = festivalUseCase.getFestival(festivalId);
        if (!festival.isDateDuringFestival(travelDate)) {
            throw new InvalidValueException(TRAVEL_DATE_NOT_IN_FESTIVAL_PERIOD);
        }
        return festival;
    }

    private List<Place> getAttractions(Festival festival, List<String> placeIds) {
        List<Place> places = placeUseCase.getFestivalNearPlacesById(festival.getId(), placeIds);
        if (places.isEmpty()) {
            places = placeUseCase.cachePlaces(festival.getId(), festival.getCoordinate()).stream()
                    .filter(place -> placeIds.contains(place.getKakaoId()))
                    .toList();
        }
        return places;
    }

    private Map<Coordinate, List<String>> mergeFestivalAndAttractionName(Festival festival, List<Place> attractions) {
        var festivalNameByCoordinate = mapFestivalNameToCoordinate(festival);
        var placeNamesByCoordinate = mapPlaceNamesToCoordinate(attractions);

        return mergeNameMaps(festivalNameByCoordinate, placeNamesByCoordinate);
    }

    private Map<Coordinate, String> mapFestivalNameToCoordinate(Festival festival) {
        return Map.of(festival.getCoordinate(), festival.getName());
    }

    private Map<Coordinate, List<String>> mapPlaceNamesToCoordinate(List<Place> attractions) {
        return attractions.stream()
                .collect(Collectors.groupingBy(
                        Place::getCoordinate, Collectors.mapping(Place::getName, Collectors.toList())
                ));
    }

    private Map<Coordinate, List<String>> mergeNameMaps(Map<Coordinate, String> festivalNameByCoordinate, Map<Coordinate, List<String>> placeNamesByCoordinate) {
        festivalNameByCoordinate.forEach((coordinate, festivalName) -> {
            placeNamesByCoordinate.merge(
                    coordinate,
                    List.of(festivalName),
                    (existingNames, newNames) -> {
                        existingNames.addAll(newNames);
                        return existingNames;
                    });
        });

        return placeNamesByCoordinate;
    }

    private List<RouteMetrics> awaitFutureResult(Future<List<RouteMetrics>> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
