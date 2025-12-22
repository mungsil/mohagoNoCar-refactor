package com.example.mohago_nocar.transit.infrastructure.route;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;
import com.example.mohago_nocar.transit.domain.model.WalkPath;
import com.example.mohago_nocar.transit.infrastructure.error.exception.ODsayRouteException;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.ODsayApiRateLimitedClient;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.ODsayTransitRouteApiAdapter;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayRouteInvalidResponse;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayRouteValidResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ODsayTransitRouteApiAdapterTest {

    @Autowired
    ODsayTransitRouteApiAdapter adapter;

    @MockBean
    ODsayApiRateLimitedClient odsayApiClient;

    @DisplayName("odsay API가 유효한 응답을 주면 TransitRoute 객체로 변환한다.")
    @Test
    public void getTransitRouteBetweenLocations_whenODsayReturnValidResponse() {
        //given
        Location origin = Location.of("출발지", Coordinate.from(126.872939584803, 37.3700357495453));
        Location dest = Location.of("도착지", Coordinate.from(126.8834795656736, 37.351812431636645));

        ODsayRouteValidResponse mockValidResponse = mock(ODsayRouteValidResponse.class);
        when(mockValidResponse.isValid()).thenReturn(true);
        when(mockValidResponse.getSubPaths()).thenReturn(List.of());
        when(odsayApiClient.searchTransitRoute(origin.getCoordinate(), dest.getCoordinate())
        ).thenReturn(mockValidResponse);

        //when
        TransitRoute transitRoute = adapter.getTransitRouteBetweenLocations(origin, dest);
        System.out.println(transitRoute);

        //then
        assertThat(transitRoute).isNotNull();
        verify(odsayApiClient).searchTransitRoute(origin.getCoordinate(), dest.getCoordinate());
    }

    @DisplayName("odsay API가 유효하지 않은 응답을 주면 예외를 발생시킨다.")
    @Test
    public void getTransitRouteBetweenLocations_whenODsayReturnInvalidResponse(){
        //given
        Location origin = Location.of("출발지", Coordinate.from(126.872939584803, 37.3700357495453));
        Location dest = Location.of("도착지", Coordinate.from(126.8834795656736, 37.351812431636645));

        ODsayRouteInvalidResponse mockInvalidResponse = mock(ODsayRouteInvalidResponse.class);
        when(mockInvalidResponse.isValid()).thenReturn(false);
        when(mockInvalidResponse.getErrorCode()).thenReturn("500");

        when(odsayApiClient.searchTransitRoute(origin.getCoordinate(), dest.getCoordinate())
        ).thenReturn(mockInvalidResponse);

        //when //then
        assertThatThrownBy(()-> adapter.getTransitRouteBetweenLocations(origin, dest))
                .isInstanceOf(ODsayRouteException.class)
                .hasMessage("ODsay 서버에 오류가 발생하였습니다. ODsay API를 확인해주세요.");
    }

    @DisplayName("ODsay API가 Distance Error 응답을 주면 도보 이동 경로를 생성한다.")
    @Test
    public void getTransitRouteBetweenLocations_whenODsayReturnShortDistanceError(){
        //given
        Location origin = Location.of("출발지", Coordinate.from(126.872939584803, 37.3700357495453));
        Location dest = Location.of("도착지", Coordinate.from(126.872939584803, 37.3700357495453));

        ODsayRouteInvalidResponse mockInvalidResponse = mock(ODsayRouteInvalidResponse.class);
        when(mockInvalidResponse.isValid()).thenReturn(false);
        when(mockInvalidResponse.getErrorCode()).thenReturn("-98");

        when(odsayApiClient.searchTransitRoute(origin.getCoordinate(), dest.getCoordinate())
        ).thenReturn(mockInvalidResponse);

        //when
        TransitRoute transitRoute = adapter.getTransitRouteBetweenLocations(origin, dest);
        System.out.println(transitRoute);

        //then
        Assertions.assertThat(transitRoute.getSubPaths().get(0))
                .isInstanceOf(WalkPath.class);
    }

}