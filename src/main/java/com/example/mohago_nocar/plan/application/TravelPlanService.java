package com.example.mohago_nocar.plan.application;

import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.festival.domain.repository.FestivalRepository;
import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.global.common.exception.InvalidValueException;
import com.example.mohago_nocar.place.domain.model.Place;
import com.example.mohago_nocar.place.domain.service.PlaceUseCase;
import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.plan.domain.service.TravelPlanUseCase;
import com.example.mohago_nocar.plan.presentation.request.PlanTravelCourseRequestDto;
import com.example.mohago_nocar.plan.presentation.response.PlanResponseDto;
import com.example.mohago_nocar.plan.presentation.response.TravelRouteResponseDto;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;
import com.example.mohago_nocar.transit.infrastructure.externalApi.google.GoogleApiClient;
import com.example.mohago_nocar.transit.infrastructure.externalApi.converter.GoogleDistanceMatrixApiConverter;
import com.example.mohago_nocar.transit.domain.converter.TransitRouteConverter;
import com.example.mohago_nocar.transit.infrastructure.externalApi.google.dto.response.RouteSpecification;
import com.example.mohago_nocar.transit.infrastructure.externalApi.odsay.ODsayApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.mohago_nocar.plan.presentation.exception.PlanErrorCode.TRAVEL_DATE_NOT_IN_FESTIVAL_PERIOD;

@Service
@RequiredArgsConstructor
@Slf4j
public class TravelPlanService implements TravelPlanUseCase {

    private final RouteOptimizationStrategy routeStrategy;
    private final FestivalRepository festivalRepository;
    private final PlaceUseCase placeUseCase;
    private final GoogleApiClient googleApiClient;
    private final ODsayApiClient oDsayApiClient;

    @Override
    public Mono<PlanResponseDto> planCourse(PlanTravelCourseRequestDto dto) {
        var festivalMono = validateAndGetFestival(dto).cache();
        var placesMono = getFestivalAroundPlaces(festivalMono, dto.placeIds()).cache().log("placesMono");

        var namesByCoordinateMono = mergeFestivalAndPlaceNames(festivalMono, placesMono);

        var optimalRouteCoordinatesMono = findOptimalRouteCoordinates(namesByCoordinateMono);
        var optimalRouteLocationsFlux = mapCoordinatesToLocations(namesByCoordinateMono, optimalRouteCoordinatesMono);

        return searchTransitInfo(optimalRouteLocationsFlux)
                .map(TravelRouteResponseDto::of)
                .collectList()
                .map(PlanResponseDto::from);
    }

    private Mono<Festival> validateAndGetFestival(PlanTravelCourseRequestDto dto) {
        return Mono.fromSupplier(()-> festivalRepository.getFestivalById(dto.festivalId()))
                .subscribeOn(Schedulers.boundedElastic())
                .filter(festival -> festival.isDateDuringFestival(dto.travelDate()))
                .switchIfEmpty(Mono.error(new InvalidValueException(TRAVEL_DATE_NOT_IN_FESTIVAL_PERIOD)));
    }

    private Mono<List<Place>> getFestivalAroundPlaces(Mono<Festival> festivalMono, List<String> placeIds) {
        return festivalMono.map(festival -> placeUseCase.getSelectedPlacesAround(festival, placeIds))
                .publishOn(Schedulers.single());
    }

    private Mono<Map<Coordinate, List<String>>> mergeFestivalAndPlaceNames(Mono<Festival> festivalMono, Mono<List<Place>> placesMono) {
        var festivalNameByCoordinate = mapFestivalNameToCoordinate(festivalMono);
        var placeNamesByCoordinate = mapPlaceNamesToCoordinate(placesMono);

        return Mono.zip(festivalNameByCoordinate, placeNamesByCoordinate)
                .map(this::mergeNameMaps);
    }

    private Mono<Map<Coordinate, String>> mapFestivalNameToCoordinate(Mono<Festival> festivalMono) {
        return festivalMono.map(festival -> Map.of(festival.getCoordinate(), festival.getName()));
    }

    private Mono<Map<Coordinate, List<String>>> mapPlaceNamesToCoordinate(Mono<List<Place>> placesMono) {
        return placesMono
                .map(places -> places.stream()
                .collect(Collectors.groupingBy(
                        Place::getCoordinate, Collectors.mapping(Place::getName, Collectors.toList())
                )));
    }

    private Map<Coordinate, List<String>> mergeNameMaps(
            Tuple2<Map<Coordinate, String>, Map<Coordinate, List<String>>> tuple
    ) {
        var festivalNameByCoordinate = tuple.getT1();
        var placeNamesByCoordinate = tuple.getT2();

        festivalNameByCoordinate.forEach((coordinate, festivalName) ->
                placeNamesByCoordinate.merge(
                        coordinate,
                        List.of(festivalName),
                        (existingNames, newNames) -> {
                            existingNames.addAll(newNames);
                            return existingNames;
                        })
        );

        return placeNamesByCoordinate;
    }

    private Mono<List<Coordinate>> findOptimalRouteCoordinates(Mono<Map<Coordinate, List<String>>> namesByCoordinate) {
        var coordinates = collectCoordinate(namesByCoordinate).log("collectCoordinate");
        var routeSpecList = fetchDistanceAndDurations(coordinates);

        return calculateRoute(coordinates, routeSpecList);
    }

    private Mono<List<Coordinate>> collectCoordinate(Mono<Map<Coordinate, List<String>>> namesByCoordinate) {
        return namesByCoordinate.map(coordinateKeyMap ->
                coordinateKeyMap.keySet().stream().toList());
    }

    /**
     * 좌표 간의 거리(km), 이동 시간(minutes)를 가져오는 외부 API를 호출하여 응답을 생성합니다.
     * @param coordinatesMono 거리, 이동 시간을 구하는 대상 좌표
     * @return 좌표 간의 거리 및 이동시간
     */
    private Mono<List<RouteSpecification>> fetchDistanceAndDurations(Mono<List<Coordinate>> coordinatesMono) {
        return coordinatesMono.flatMap(coordinates ->
                Flux.range(0, coordinates.size())
                        .flatMap(index -> getRouteSpecificationFlux(coordinates, index))
                        .collectList()
        );
    }

    private Flux<RouteSpecification> getRouteSpecificationFlux(List<Coordinate> coordinates, Integer index) {
        var origin = coordinates.get(index);
        var destinations = createDestination(coordinates, index);

        var response = googleApiClient.getDistanceMatrix(origin, destinations);

        return response
                .flatMapMany(matrixResponse ->
                GoogleDistanceMatrixApiConverter.convertToRouteSpecification(response, origin, destinations)
        );
    }

    private List<Coordinate> createDestination(List<Coordinate> coordinates, int excludeIndex) {
        return IntStream.range(0, coordinates.size())
                .filter(index -> index != excludeIndex)
                .mapToObj(coordinates::get)
                .toList();
    }

    private Mono<List<Coordinate>> calculateRoute(
            Mono<List<Coordinate>> coordinates,
            Mono<List<RouteSpecification>> routeSpecList
    ) {
        return Mono.zip(coordinates, routeSpecList)
                .publishOn(Schedulers.parallel())
                .map(tuple -> routeStrategy.calculateOptimalRoute(tuple.getT1(), tuple.getT2()))
                .publishOn(Schedulers.single())
                ;
    }

    private Flux<Location> mapCoordinatesToLocations(
            Mono<Map<Coordinate, List<String>>> namesByCoordinateMono,
            Mono<List<Coordinate>> coordinatesMono
    ) {
        return Mono.zip(namesByCoordinateMono, coordinatesMono)
                .flatMapMany(tuple -> {
                    var namesByCoordinate = tuple.getT1();
                    var coordinates = tuple.getT2();

                    return createLocations(coordinates, namesByCoordinate);
                });
    }

    private Flux<Location> createLocations(
            List<Coordinate> coordinates,
            Map<Coordinate, List<String>> namesByCoordinate
    ) {
        return Flux.fromIterable(coordinates)
                .flatMapSequential(coordinate -> createLocationFromCoordinate(namesByCoordinate, coordinate));
    }

    private Flux<Location> createLocationFromCoordinate(Map<Coordinate, List<String>> namesByCoordinate, Coordinate coordinate) {
        List<String> names = namesByCoordinate.get(coordinate);
        return Flux.fromIterable(names)
                .map(name -> Location.of(name, coordinate));
    }

    private Flux<TransitRoute> searchTransitInfo(Flux<Location> location) {
        return location.collectList()
                .flatMapMany(locationList ->
                        Flux.range(0, locationList.size() - 1)
                                .flatMap(index -> {
                                    var origin = locationList.get(index);
                                    var destination = locationList.get(index + 1);

                                    return getTransitRoute(origin, destination);
                                })
                );
    }

    private Mono<TransitRoute> getTransitRoute(Location origin, Location destination) {
        return oDsayApiClient.searchTransitRoute2(origin.getCoordinate(), destination.getCoordinate())
                .map(routeResponse ->
                        TransitRouteConverter.convertRouteResponseDtoToTransitRoute(routeResponse, origin, destination)
                );
    }

}
