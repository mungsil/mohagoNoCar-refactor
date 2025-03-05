package com.example.mohago_nocar.transit.infrastructure.distanceDuration.google;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.dto.response.GoogleDistanceMatrixResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;


@SpringBootTest
@ActiveProfiles("test")
class GoogleApiClientTest {

    @Autowired
    private GoogleApiClient googleApiClient;

    @DisplayName("출발지와 도착지 사이의 거리 및 이동 시간을 조회할 수 있다.")
    @Test
    public void getDistanceMatrix() {
        //given
        Coordinate origin = Coordinate.from(126.872939584803, 37.3700357495453);

        Coordinate dest1 = Coordinate.from(126.8834795656736, 37.351812431636645);
        Coordinate dest2 = Coordinate.from(126.899445340496, 37.3673238473972);
        Coordinate dest3 = Coordinate.from(126.848208105819, 37.3649832880928);

        List<Coordinate> destinations = List.of(dest1, dest2, dest3);

        //when
        GoogleDistanceMatrixResponse response = googleApiClient.getDistanceMatrix(origin, destinations);

        //then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.rows().get(0).elements().size()).isEqualTo(3);

        for (GoogleDistanceMatrixResponse.Element element : response.rows().get(0).elements()) {
            Assertions.assertThat(element.distance().text()).isNotNull();
            Assertions.assertThat(element.duration().text()).isNotNull();
        }
    }

}