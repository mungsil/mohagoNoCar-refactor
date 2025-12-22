package com.example.mohago_nocar.transit.infrastructure.route.odsay;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.global.rateLimit.RateLimiter;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayTransitRouteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

// todo 클래스명 수정
@Component
@Slf4j
public class ODsayApiRateLimitedClient {

    private final OdsayApiProperties apiProperties;
    private final RateLimiter rateLimiter;
    private final WebClient webClient;

    public ODsayApiRateLimitedClient(
            OdsayApiProperties apiProperties
    ) {
        this.apiProperties = apiProperties;
        this.rateLimiter = RateLimiter.create(apiProperties.getExecutionIntervalMills());
        this.webClient = WebClient.builder().build();
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
        URI requestURI = buildRequestURI(origin, destination);
        rateLimiter.throttle();
        return executeApiCall(requestURI);
    }

    /**
     * 대중교통 경로를 검색합니다. API 응답을 비동기로 처리합니다.
     *
     * <p><b>Rate Limit 제어:</b> API 키 획득 시 rate limit 준수를 위해
     * 의도적으로 블로킹하여 소비 속도를 조절합니다.</p>
     *
     * @param origin 출발지 좌표
     * @param destination 도착지 좌표
     * @return 경로 검색 결과의 CompletableFuture
     */
    public CompletableFuture<ODsayTransitRouteResponse> searchTransitRouteAsync(
            Coordinate origin, Coordinate destination) {
        URI requestURI = buildRequestURI(origin, destination);
        rateLimiter.throttle();
        return executeApiCallAsync(requestURI);
    }

    private URI buildRequestURI(Coordinate origin, Coordinate destination) {
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromUriString(apiProperties.getRequestUrl())
                        .queryParam("SX", origin.getLongitude())
                        .queryParam("SY", origin.getLatitude())
                        .queryParam("EX", destination.getLongitude())
                        .queryParam("EY", destination.getLatitude());

        return uriComponentsBuilder
                .queryParam("apiKey", apiProperties.getEncodedKey())
                .build(true)
                .toUri();
    }

    private ODsayTransitRouteResponse executeApiCall(URI requestURI) {
        return webClient.get()
                .uri(requestURI)
                .retrieve()
                .bodyToMono(ODsayTransitRouteResponse.class)
                .block();
    }

    private CompletableFuture<ODsayTransitRouteResponse> executeApiCallAsync(URI requestURI) {
        return webClient.get()
                .uri(requestURI)
                .retrieve()
                .bodyToMono(ODsayTransitRouteResponse.class)
                .toFuture();
    }

}
