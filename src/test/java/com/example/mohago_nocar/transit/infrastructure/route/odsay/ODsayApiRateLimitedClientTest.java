package com.example.mohago_nocar.transit.infrastructure.route.odsay;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayRouteInvalidResponse;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayRouteValidResponse;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayTransitRouteResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ODsayApiRateLimitedClientTest {

    @Autowired
    private ODsayApiRateLimitedClient odsayApiClient;

    @DisplayName("출발지와 도착지 간의 대중교통 경로를 조회할 수 있다.")
    @Test
    public void searchTransitRoute(){
        //given
        Coordinate origin = Coordinate.from(126.872939584803, 37.3700357495453);
        Coordinate dest = Coordinate.from(126.8834795656736, 37.351812431636645);

        //when
        ODsayTransitRouteResponse response = odsayApiClient.searchTransitRoute(origin, dest);

        //then
        assertThat(response).isInstanceOf(ODsayTransitRouteResponse.class);
        assertThat(response).isInstanceOf(ODsayRouteValidResponse.class);
    }

    @DisplayName("출발지와 도착지 간의 거리가 700m 이내이면 유효하지 않은 응답이 반환된다.")
    @Test
    public void searchTransitRoute_whenDistanceLessThan700Meter(){
        //given
        Coordinate origin = Coordinate.from(126.872939584803, 37.3700357495453);
        Coordinate dest = Coordinate.from(126.872939584803, 37.3700357495453);

        //when
        ODsayTransitRouteResponse response = odsayApiClient.searchTransitRoute(origin, dest);

        //then
        assertThat(response).isInstanceOf(ODsayTransitRouteResponse.class);
        assertThat(response).isInstanceOf(ODsayRouteInvalidResponse.class);
    }

/*    @DisplayName("호출 간격을 준수하며 API 요청을 보낸다.")
    @Test
    public void searchTransitRoute_withRateLimit(){
        //given
        Coordinate origin = Coordinate.from(126.872939584803, 37.3700357495453);
        Coordinate dest = Coordinate.from(126.8834795656736, 37.351812431636645);

        //when //then
        List<ODsayTransitRouteResponse> responses = IntStream.range(0, 10)
                .mapToObj(i -> odsayApiClient.searchTransitRoute(origin, dest))
                .filter(response -> response instanceof ODsayRouteValidResponse)
                .toList();

        assertThat(responses).hasSize(10);
    }*/

}