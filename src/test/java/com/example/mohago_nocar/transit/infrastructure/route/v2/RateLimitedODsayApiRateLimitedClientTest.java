package com.example.mohago_nocar.transit.infrastructure.route.v2;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.support.IntegrationTestSupport;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.ODsayApiRateLimitedClient;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayTransitRouteResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitedODsayApiRateLimitedClientTest extends IntegrationTestSupport {

    @Autowired
    private ODsayApiRateLimitedClient odsayApiClient;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREAN);

    @DisplayName("출발지와 도착지 간의 대중교통 경로를 429에러 없이 조회한다.")
    @Test
    public void safeSearchTransitRoute() throws InterruptedException {
        System.out.println("아!아! 마이크테스트! 시작합니다- : " + formatter.format(LocalDateTime.now()));

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            CompletableFuture<Void> future =
                    CompletableFuture.runAsync(() -> callAPI());
            futures.add(future);
        }

        // 모든 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        System.out.println("끝났어요!: " + formatter.format(LocalDateTime.now()));
    }

    private void callAPI() {
        //given
        Coordinate origin = Coordinate.from(126.872939584803, 37.3700357495453);
        Coordinate dest = Coordinate.from(126.8834795656736, 37.351812431636645);

        //when
        ODsayTransitRouteResponse response = odsayApiClient.searchTransitRoute(origin, dest);

        //then
        System.out.println(response);
    }

}