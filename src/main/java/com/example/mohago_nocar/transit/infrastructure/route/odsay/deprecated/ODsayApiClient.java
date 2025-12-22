package com.example.mohago_nocar.transit.infrastructure.route.odsay.deprecated;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayTransitRouteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// todo 클래스명 수정
@Deprecated
@Component
@Slf4j
public class ODsayApiClient {

    private final WebClient webClient;
    private final String baseUrl;
    private final String apiKey;

    public ODsayApiClient(
            @Value("${odsay.request-url}") String baseUrl,
            @Value("${odsay.key}") String apiKey
    ) {
        this.webClient = WebClient.builder().build();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    /**
     * 대중교통 경로를 검색합니다.
     *
     * <p><b>Rate Limit 제어:</b> API 키 획득 시 rate limit 준수를 위해
     * 의도적으로 블로킹하여 소비 속도를 조절합니다.</p>
     *
     * @param origin 출발지 좌표
     * @param destination 도착지 좌표
     * @return 경로 검색 결과의 CompletableFuture
     */
    public ODsayTransitRouteResponse searchTransitRoute(Coordinate origin, Coordinate destination) {
        String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
        URI requestURI = buildRequestURI(origin, destination, encodedKey);
        ODsayTransitRouteResponse response = executeApiCall(requestURI);
        return response;
    }

    private ODsayTransitRouteResponse executeApiCall(URI requestURI) {
        return webClient.get()
                .uri(requestURI)
                .retrieve()
                .bodyToMono(ODsayTransitRouteResponse.class)
                .block();
    }

    private URI buildRequestURI(Coordinate origin, Coordinate destination, String encodedKey) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("SX", origin.getLongitude())
                .queryParam("SY", origin.getLatitude())
                .queryParam("EX", destination.getLongitude())
                .queryParam("EY", destination.getLatitude());

        return uriComponentsBuilder
                .queryParam("apiKey", encodedKey)
                .build(true)
                .toUri();
    }

}
