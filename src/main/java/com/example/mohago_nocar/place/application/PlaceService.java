package com.example.mohago_nocar.place.application;

import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.festival.domain.service.FestivalUseCase;
import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.place.domain.converter.PlaceConverter;
import com.example.mohago_nocar.place.domain.model.Place;
import com.example.mohago_nocar.place.domain.repository.PlaceRepository;
import com.example.mohago_nocar.place.domain.service.PlaceUseCase;
import com.example.mohago_nocar.place.infrastructure.externalApi.kakao.KakaoApiClient;
import com.example.mohago_nocar.place.infrastructure.externalApi.kakao.dto.response.KakaoPlacesResponse;
import com.example.mohago_nocar.place.presentation.response.NearPlaceResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService implements PlaceUseCase {

    private static final int RADIUS = 3000;
    private static final int PAGE_SIZE = 10;

    private final FestivalUseCase festivalUseCase;
    private final PlaceRepository placeRepository;
    private final KakaoApiClient kakaoApiClient;

    @Transactional
    @Override
    public List<NearPlaceResponseDto> getPlacesAround(Long festivalId) {
        Festival festival = festivalUseCase.getFestival(festivalId);
        List<Place> places = placeRepository.getFestivalAroundPlaces(festivalId);
        if (places.isEmpty()) {
            places = cacheFestivalAroundPlaces(festivalId, festival.getCoordinate());
        }

        return PlaceConverter.convertToNearPlaceResponseDtos(festivalId, places);
    }

    @Transactional
    @Override
    public List<Place> getSelectedPlacesAround(Festival festival, List<String> selectedIds) {
        List<Place> places = getChosenPlace(festival.getId(), selectedIds);
        if (places.isEmpty()) {
            places = cacheFestivalAroundPlaces(festival.getId(), festival.getCoordinate()).stream()
                    .filter(place -> selectedIds.contains(place.getId()))
                    .toList();
        }
        return places;
    }

    private List<Place> cacheFestivalAroundPlaces(Long festivalId, Coordinate festivalCoordinate) {
        KakaoPlacesResponse response = searchPlacesAround(festivalCoordinate);

        return placeRepository.savePlaces(festivalId, PlaceConverter.convertToPlaces(response));
    }

    private List<Place> getChosenPlace(Long festivalId, List<String> selectedPlaceIds) {
        List<Place> allPlacesAroundFestival = placeRepository.getFestivalAroundPlaces(festivalId);
        return filterSelectedPlaces(selectedPlaceIds, allPlacesAroundFestival);
    }

    private List<Place> filterSelectedPlaces(List<String> chosenPlaceIds, List<Place> allPlacesAroundFestival) {
        return allPlacesAroundFestival.stream()
                .filter(place -> chosenPlaceIds.contains(place.getId()))
                .toList();
    }

    private KakaoPlacesResponse searchPlacesAround(Coordinate centerCoordinate) {
        return kakaoApiClient.searchAttractionPlaces(
                centerCoordinate,
                RADIUS,
                PAGE_SIZE
        );
    }

}