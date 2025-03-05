package com.example.mohago_nocar.transit.infrastructure.distanceDuration;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.domain.model.RouteMetrics;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.GoogleApiClient;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.dto.response.GoogleDistanceMatrixResponse;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.dto.response.GoogleDistanceMatrixStatus;
import com.example.mohago_nocar.transit.infrastructure.error.exception.DistanceMatrixException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class GoogleDistanceMatrixApiAdapterTest {

    @Autowired
    private GoogleDistanceMatrixApiAdapter googleDistanceMatrixApiAdapter;

    @SpyBean
    private GoogleApiClient googleApiClient;

    @DisplayName("유효하지 않은 API 응답임이 확인되면 예외를 throw 한다.")
    @Test
    public void getDistanceAndDuration_throwException_if_invalid() {
        //given
        Coordinate origin = Coordinate.from(126.872939584803, 37.3700357495453);
        List<Coordinate> destinations = createDestinations();

        when(googleApiClient.getDistanceMatrix(origin, destinations))
                .thenReturn(new GoogleDistanceMatrixResponse(null, null, null, GoogleDistanceMatrixStatus.INVALID_REQUEST));

        //when //then
        assertThatThrownBy(() -> googleDistanceMatrixApiAdapter.getDistanceAndDuration(origin, destinations))
                .isInstanceOf(DistanceMatrixException.class)
                .hasMessage("잘못된 요청입니다");
    }

    @DisplayName("API 응답에 NULL이 포함되어 있으면 예외를 throw 한다.")
    @Test
    public void getDistanceAndDuration_catchException_if_null() {
        //given
        Coordinate origin = Coordinate.from(126.872939584803, 37.3700357495453);
        List<Coordinate> destinations = createDestinations();

        when(googleApiClient.getDistanceMatrix(origin, destinations))
                .thenReturn(new GoogleDistanceMatrixResponse(null, null, null, GoogleDistanceMatrixStatus.OK));

        //when //then
        assertThatThrownBy(() -> googleDistanceMatrixApiAdapter.getDistanceAndDuration(origin, destinations))
                .isInstanceOf(DistanceMatrixException.class)
                .hasMessage("외부 서버 오류가 발생했습니다");

    }

    @DisplayName("출발지와 도착지 간의 이동 거리와 시간을 알 수 있다.")
    @Test
    public void getDistanceAndDuration(){
        //given
        Coordinate origin = Coordinate.from(126.872939584803, 37.3700357495453);
        List<Coordinate> destinations = createDestinations();

        //when
        List<RouteMetrics> routeMetrics = googleDistanceMatrixApiAdapter.getDistanceAndDuration(origin, destinations);

        //then
        Assertions.assertThat(routeMetrics).isNotNull();

    }

    private List<Coordinate> createDestinations() {
        Coordinate dest1 = Coordinate.from(126.8834795656736, 37.351812431636645);
        Coordinate dest2 = Coordinate.from(126.899445340496, 37.3673238473972);
        Coordinate dest3 = Coordinate.from(126.848208105819, 37.3649832880928);

        return List.of(dest1, dest2, dest3);
    }

}