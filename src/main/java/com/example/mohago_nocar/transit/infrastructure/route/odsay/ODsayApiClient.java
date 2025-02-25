package com.example.mohago_nocar.transit.infrastructure.route.odsay;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.global.common.exception.InternalServerException;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.dto.response.ODsayTransitRouteResponse;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.time.Duration;

@Component
@Slf4j
public class ODsayApiClient {

    private static final int TIMEOUT_DURATION_SEC = 30;
    private static final int INTERVAL_MS = 210;
    private static final int PERMIT_THREAD_SIZE = 1;
    private static final String ODSAY_RATE_LIMITER = "odsay";

    private final RestClient restClient;
    private final String apiKey;
    private final String baseUrl;

    private final RateLimiter rateLimiter;

    public ODsayApiClient(
            RestClient restClient,
            @Value("${odsay.api-key}") String apiKey,
            @Value("${odsay.url}") String baseUrl
    ) {
        RateLimiterRegistry rateLimiterRegistry = initializeRateLimiter();
        this.rateLimiter = rateLimiterRegistry.rateLimiter(ODSAY_RATE_LIMITER);

        this.restClient = restClient;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public ODsayTransitRouteResponse searchTransitRoute(Coordinate origin, Coordinate destination) {
        URI requestURI = buildRequestURI(origin, destination);

        return rateLimiter.executeSupplier(() -> executeApiCall(requestURI));
    }

    private ODsayTransitRouteResponse executeApiCall(URI requestURI) {
        return restClient.get()
                .uri(requestURI)
                .retrieve()
                .body(ODsayTransitRouteResponse.class);
    }

    private URI buildRequestURI(Coordinate origin, Coordinate destination) {
        String encodedApiKey = createEncodedApiKey();

        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("SX", origin.getLongitude())
                .queryParam("SY", origin.getLatitude())
                .queryParam("EX", destination.getLongitude())
                .queryParam("EY", destination.getLatitude())
                .queryParam("apiKey", encodedApiKey)
                .build(true)
                .toUri();
    }

    private String createEncodedApiKey() {
        try {
            return URLEncoder.encode(apiKey, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

    private RateLimiterRegistry initializeRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMillis(INTERVAL_MS))
                .limitForPeriod(PERMIT_THREAD_SIZE)
                .timeoutDuration(Duration.ofSeconds(TIMEOUT_DURATION_SEC))
                .build();

        return RateLimiterRegistry.of(config);
    }

}
