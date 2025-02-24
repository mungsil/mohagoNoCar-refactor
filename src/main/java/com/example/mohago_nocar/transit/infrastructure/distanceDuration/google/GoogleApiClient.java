package com.example.mohago_nocar.transit.infrastructure.externalApi.google;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.dto.response.GoogleDistanceMatrixResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoogleApiClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String baseUrl;

    public GoogleApiClient(
            RestClient restClient,
            @Value("${google.api-key}") String apiKey,
            @Value("${google.maps.distance}") String baseUrl
    ) {
        this.restClient = restClient;
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
    public GoogleDistanceMatrixResponse getDistanceMatrix(Location origin, List<Location> destinations) {
        URI requestUri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("origins", formatOriginCoordinates(origin))
                .queryParam("destinations", formatLocations(destinations))
                .queryParam("language", "ko")
                .queryParam("mode", "transit")
                .queryParam("key", apiKey)
                .build(true)
                .toUri();

        return restClient.get()
                .uri(requestUri)
                .retrieve()
                .body(GoogleDistanceMatrixResponse.class);
    }

    private String formatCoordinates(Coordinate origin) {
        return origin.getLatitude() + "," + origin.getLongitude();
    }

    private String formatCoordinates(List<Coordinate> coordinates) {
        return URLEncoder.encode(
                coordinates.stream()
                        .map(location -> location.getLatitude() + "," + location.getLongitude())
                        .collect(Collectors.joining("|"))
                ,StandardCharsets.UTF_8
        );
    }

}
