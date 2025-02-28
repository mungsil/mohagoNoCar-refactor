package com.example.mohago_nocar.place.infrastructure;

import com.example.mohago_nocar.global.common.exception.EntityNotFoundException;
import com.example.mohago_nocar.global.util.ObjectMapperUtil;
import com.example.mohago_nocar.place.domain.model.Place;
import com.example.mohago_nocar.place.domain.repository.PlaceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.mohago_nocar.place.presentation.exception.PlaceErrorCode.PLACE_NOT_FOUND;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepository {

    private static final String KEY_PREFIX = "festival:places:";
    private static final int TIME_TO_LIVE_HOURS = 2;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapperUtil objectMapperUtil;

    @Override
    public List<Place> getChosenPlace(Long festivalId, List<String> selectedPlaceIds) {
        List<Place> allPlacesAroundFestival = getFestivalAroundPlaces(festivalId);
        return filterSelectedPlaces(selectedPlaceIds, allPlacesAroundFestival);
    }

    private List<Place> filterSelectedPlaces(List<String> chosenPlaceIds, List<Place> allPlacesAroundFestival) {
        return allPlacesAroundFestival.stream()
                .filter(place -> chosenPlaceIds.contains(place.getId()))
                .toList();
    }

    @Override
    public List<Place> getFestivalAroundPlaces(Long festivalId) {
        String placesInJson = findPlacesFromCache(generateCacheKey(festivalId));

        if (isEmpty(placesInJson)) {
            return Collections.emptyList();
        }

        return objectMapperUtil.readValue(placesInJson, new TypeReference<>() {
        });
    }

    @Override
    public List<Place> savePlaces(Long festivalId, List<Place> toSavePlaces) {
        String key = generateCacheKey(festivalId);
        String placesJson = objectMapperUtil.writeValue(toSavePlaces);
        redisTemplate.opsForValue().set(key, placesJson, TIME_TO_LIVE_HOURS, TimeUnit.HOURS);

        return findPlaces(key);
    }

    private List<Place> findPlaces(String key) {
        String placesInJson = findPlacesFromCache(key);

        if (isEmpty(placesInJson)) {
            throw new EntityNotFoundException(PLACE_NOT_FOUND);
        }

        return parsePlaces(placesInJson);
    }

    private List<Place> parsePlaces(String json) {
        return objectMapperUtil.readValue(json, new TypeReference<>() {
        });
    }

    private boolean isEmpty(String placesInJson) {
        return StringUtils.isEmpty(placesInJson);
    }

    private String findPlacesFromCache(String redisKey) {
        return redisTemplate.opsForValue().get(redisKey);
    }

    private String generateCacheKey(Long festivalId) {
        return KEY_PREFIX + festivalId;
    }

}
