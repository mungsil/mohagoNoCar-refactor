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
import com.example.mohago_nocar.place.presentation.NearPlaceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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
    public List<NearPlaceResponseDto> getFestivalNearPlaces(Long festivalId) {
        Festival festival = festivalUseCase.getFestival(festivalId);
        List<Place> places = placeRepository.getFestivalAroundPlaces(festivalId);
        if (places.isEmpty()) {
            places = cachePlaces(festivalId, festival.getCoordinate());
        }

        return PlaceConverter.convertToNearPlaceResponseDtos(festivalId, places);
    }

    public List<Place> cachePlaces(Long festivalId, Coordinate centerCoordinate) {
        KakaoPlacesResponse placesFromExternalApi = searchPlacesAround(centerCoordinate);
        List<Place> places = PlaceConverter.convertToPlaces(placesFromExternalApi);
        return placeRepository.saveAllToCache(festivalId, places);
    }

    private KakaoPlacesResponse searchPlacesAround(Coordinate centerCoordinate) {
        return kakaoApiClient.searchAttractionPlaces(
                centerCoordinate,
                RADIUS,
                PAGE_SIZE
        );
    }

}