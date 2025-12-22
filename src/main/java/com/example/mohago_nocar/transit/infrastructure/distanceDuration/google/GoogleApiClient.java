package com.example.mohago_nocar.transit.infrastructure.distanceDuration.google;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.dto.response.GoogleDistanceMatrixResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GoogleApiClient {

    private final WebClient webClient;
    private final String apiKey;
    private final String baseUrl;

    private static final String DELIMITER_COMMA = "," ;
    private static final String PIPE = "|" ;

    public GoogleApiClient(
            @Value("${google.api-key}") String apiKey,
            @Value("${google.maps.distance}") String baseUrl
    ) {
        this.webClient = WebClient.builder().build();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    /**
     * 하나의 출발지와 여러 목적지로 이루어진 행렬을 반환합니다.
     * 각 셀의 값은 `(거리, 소요 시간)` 형식입니다.
     *
     * <table>
     *   <tr>
     *     <th></th> <th>Destination 1</th> <th>Destination 2</th> <th>Destination 3</th>
     *   </tr>
     *   <tr>
     *     <th>Origin 1</th> <td>Value 1</td> <td>Value 2</td> <td>Value 3</td>
     *   </tr>
     * </table>
     * @return 행렬에 기반한 (출발지, 목적지)와 관련된 데이터를 반환합니다.
     */
    public GoogleDistanceMatrixResponse getDistanceMatrix(Coordinate origin, List<Coordinate> destinations) {
        URI requestUri = buildUri(origin, destinations.stream());
        return executeApiCall(requestUri);
    }

    public CompletableFuture<GoogleDistanceMatrixResponse> getDistanceMatrixAsync(Coordinate origin, Set<Coordinate> destinations) {
        URI requestUri = buildUri(origin, destinations.stream());
        return executeApiCallAsync(requestUri);
    }

    private URI buildUri(Coordinate origin, Stream<Coordinate> destinations) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("origins", encodeCoordinate(origin))
                .queryParam("destinations", encodeCoordinates(destinations))
                .queryParam("language", "ko")
                .queryParam("mode", "transit")
                .queryParam("key", apiKey)
                .build(true)
                .toUri();
    }

    private CompletableFuture<GoogleDistanceMatrixResponse> executeApiCallAsync(URI requestUri) {
        return webClient.get()
                .uri(requestUri)
                .retrieve()
                .bodyToMono(GoogleDistanceMatrixResponse.class)
                .toFuture();
    }

    private GoogleDistanceMatrixResponse executeApiCall(URI requestUri) {
        return webClient.get()
                .uri(requestUri)
                .retrieve()
                .bodyToMono(GoogleDistanceMatrixResponse.class)
                .block();
    }

    private String encodeCoordinate(Coordinate origin) {
        return origin.getLatitude() + DELIMITER_COMMA + origin.getLongitude();
    }

    /**
     * Coordinate 스트림을 "위도,경도|위도,경도|..." 형태로 변환 후 URL 인코딩합니다.
     */
    private String encodeCoordinates(Stream<Coordinate> coordinates) {
        return URLEncoder.encode(
                coordinates
                        .map(location -> location.getLatitude() + DELIMITER_COMMA + location.getLongitude())
                        .collect(Collectors.joining(PIPE)),
                StandardCharsets.UTF_8
        );
    }

}
