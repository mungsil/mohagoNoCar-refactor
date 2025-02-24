package com.example.mohago_nocar.plan.application;

import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.festival.domain.repository.FestivalRepository;
import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.global.common.exception.InvalidValueException;
import com.example.mohago_nocar.place.application.PlaceService;
import com.example.mohago_nocar.place.domain.model.Place;
import com.example.mohago_nocar.place.domain.repository.PlaceRepository;
import com.example.mohago_nocar.plan.application.strategy.RouteOptimizationStrategy;
import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.plan.domain.service.TravelPlanUseCase;
import com.example.mohago_nocar.plan.presentation.request.PlanTravelCourseRequestDto;
import com.example.mohago_nocar.plan.presentation.response.PlanTravelCourseResponseDto;
import com.example.mohago_nocar.plan.presentation.response.TravelRouteResponseDto;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.DistanceDurationApiAdapter;
import com.example.mohago_nocar.transit.infrastructure.route.TransitRouteApiAdapter;
import com.example.mohago_nocar.transit.domain.model.RouteMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.mohago_nocar.plan.presentation.exception.PlanErrorCode.TRAVEL_DATE_NOT_IN_FESTIVAL_PERIOD;

@Service
@RequiredArgsConstructor
@Slf4j
public class TravelPlanService implements TravelPlanUseCase {

    private final PlaceRepository placeRepository;
    private final FestivalRepository festivalRepository;
    private final PlaceService placeService;
    private final RouteOptimizationStrategy routeOptimizationStrategy;
    private final ExecutorService executor;
    private final TransitRouteApiAdapter transitRouteApiAdapter;
    private final DistanceDurationApiAdapter distanceDurationApiAdapter;

    @Override
    public PlanTravelCourseResponseDto planCourse(PlanTravelCourseRequestDto dto) {
        Festival festival = validateAndGetFestival(dto);
        List<Place> attractions = getAttractions(festival, dto.placeIds());

        Map<Coordinate, List<String>> namesByCoordinate = mergeFestivalAndAttractionName(festival, attractions);

        List<Coordinate> optimalRouteCoordinates = findOptimalRoute(namesByCoordinate);
        List<Location> optimalRouteLocations = mapCoordinatesToLocations(namesByCoordinate, optimalRouteCoordinates);

        List<TravelRouteResponseDto> responses = searchTransitRoutes(optimalRouteLocations).stream()
                .map(TravelRouteResponseDto::of)
                .toList();

        return PlanTravelCourseResponseDto.of(responses);
    }

    private List<Coordinate> findOptimalRoute(Map<Coordinate, List<String>> namesByCoordinate) {
        var coordinates = collectCoordinate(namesByCoordinate);
        var routeMetrics = fetchDistanceAndDurations(coordinates);

        return routeOptimizationStrategy.calculateOptimalRoute(coordinates, routeMetrics);
    }

    private List<Coordinate> collectCoordinate(Map<Coordinate, List<String>> namesByCoordinate) {
        return namesByCoordinate.keySet().stream().toList();
    }

    /**
     * 좌표 간의 거리(km), 이동 시간(minutes)를 가져오는 외부 API를 호출하여 응답을 생성합니다.
     * @param coordinates 거리, 이동 시간을 구하는 대상 좌표
     * @return 좌표 간의 거리 및 이동시간
     */
    private List<RouteMetrics> fetchDistanceAndDurations(List<Coordinate> coordinates) {
        var futures = IntStream.range(0, coordinates.size())
                .mapToObj(index -> asyncGetDistanceDuration(coordinates, index))
                .toList();

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<TransitRoute> searchTransitRoutes(List<Location> optimalRouteLocations) {
        return IntStream.range(0, optimalRouteLocations.size() - 1)
                .mapToObj(index -> {
                    Location origin = optimalRouteLocations.get(index);
                    Location destination = optimalRouteLocations.get(index + 1);

                    return transitRouteApiAdapter.getTransitRouteBetweenLocations(origin, destination);
                })
                .toList();
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

    private CompletableFuture<List<RouteMetrics>> asyncGetDistanceDuration(List<Coordinate> coordinates, int index) {
        return CompletableFuture.supplyAsync(() -> distanceDurationApiCall(index, coordinates), executor);
    }

    private List<RouteMetrics> distanceDurationApiCall(int index, List<Coordinate> coordinates) {
        Coordinate origin = coordinates.get(index);
        List<Coordinate> destinations = createDestination(coordinates, index);

        return distanceDurationApiAdapter.getDistanceAndDuration(origin, destinations);
    }

    private List<Coordinate> createDestination(List<Coordinate> coordinates, int excludeIndex) {
        return IntStream.range(0, coordinates.size())
                .filter(index -> index != excludeIndex)
                .mapToObj(coordinates::get)
                .toList();
    }

    private Festival validateAndGetFestival(PlanTravelCourseRequestDto dto) {
        var festival = festivalRepository.getFestivalById(dto.festivalId());
        ensureTravelDateDuringFestival(festival, dto.travelDate());
        return festival;
    }

    private void ensureTravelDateDuringFestival(Festival festival, LocalDate travelDate) {
        if (!festival.isDateDuringFestival(travelDate)) {
            throw new InvalidValueException(TRAVEL_DATE_NOT_IN_FESTIVAL_PERIOD);
        }
    }

    private List<Place> getAttractions(Festival festival, List<String> placeIds) {
        List<Place> places = placeRepository.findByIds(festival.getId(), placeIds);
        if (places.isEmpty()) {
            places = placeService.cachePlaces(festival.getId(), festival.getCoordinate()).stream()
                    .filter(place -> placeIds.contains(place.getId()))
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

}
