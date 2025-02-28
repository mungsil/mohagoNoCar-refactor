package com.example.mohago_nocar.transit.infrastructure.externalApi.odsay;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.infrastructure.error.exception.OdsayTooManyRequestsException;
import com.example.mohago_nocar.transit.infrastructure.externalApi.odsay.dto.response.OdsayRouteResponse;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * ODsay API 클라이언트: 대중교통 경로 검색 요청을 처리하고 결과를 반환합니다.
 */
@Component
@Slf4j
public class ODsayApiClient {

    private final WebClient webClient;
    private final RateLimiter rateLimiter;
    private final String apiKey;

    public ODsayApiClient(
            WebClient.Builder odsayWebClient,
            @Value("${odsay.api-key}") String apiKey,
            @Value("${odsay.url}") String baseUrl
    ) {
        this.webClient = odsayWebClient.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.rateLimiter = RateLimiter.of("odsay-rate-limiter",
                RateLimiterConfig.custom()
                        .limitRefreshPeriod(Duration.ofMillis(210))
                        .limitForPeriod(1)
                        .timeoutDuration(Duration.ofMinutes(1)) // max wait time for a request, if reached then error
//                        .timeoutDuration(Duration.ofMillis(1))
                        .build());
    }

    public Mono<OdsayRouteResponse> searchTransitRoute2(Coordinate origin, Coordinate destination) {
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .queryParam("SX", origin.getLongitude())
                                .queryParam("SY", origin.getLatitude())
                                .queryParam("EX", destination.getLongitude())
                                .queryParam("EY", destination.getLatitude())
                                .queryParam("apiKey", "{key}")
                                .build(apiKey)
                )
                .retrieve()
                .bodyToMono(OdsayRouteResponse.class)
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(210))
                        .filter(throwable -> throwable instanceof OdsayTooManyRequestsException)
                        .maxBackoff(Duration.ofSeconds(1))
                        .jitter(0.5));
    }

}
