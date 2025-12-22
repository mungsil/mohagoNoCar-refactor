package com.example.mohago_nocar.place.infrastructure.externalApi.kakao;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.place.domain.model.PlaceCategory;
import com.example.mohago_nocar.place.infrastructure.externalApi.kakao.dto.response.KakaoPlacesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class KakaoApiClient {

    private static final String AUTHORIZATION_PREFIX = "KakaoAK ";

    private final String baseUrl;
    private final String apiKey;
    private final WebClient webClient;

    public KakaoApiClient(
            @Value("${kakao.local.category}") String baseUrl,
            @Value("${kakao.api-key}") String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.webClient = WebClient.builder().build();
    }

    public KakaoPlacesResponse searchAttractionPlaces(Coordinate centerCoordinate, int radius, int size) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("x", centerCoordinate.getLongitude())
                .queryParam("y", centerCoordinate.getLatitude())
                .queryParam("radius", radius)
                .queryParam("size", size)
                .queryParam("category_group_code", PlaceCategory.ATTRACTION.getCode())
                .build(true)
                .toUri();

        return webClient.get()
                .uri(uri)
                .header("Authorization", AUTHORIZATION_PREFIX + apiKey)
                .retrieve()
                .bodyToMono(KakaoPlacesResponse.class)
                .block();
    }

}
