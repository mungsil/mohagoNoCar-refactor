package com.example.mohago_nocar.transit.infrastructure.route.odsay;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.support.LocalIntegrationTestSupport;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.deprecated.ODsayApiClient;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayRouteInvalidResponse;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayTransitRouteResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class ODsayApiRateLimitingTest extends LocalIntegrationTestSupport {

    @Autowired
    private ODsayApiClient odsayApiClient;

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREAN);

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Test
    @DisplayName("속도를 측정한다.")
    void testLimit() throws InterruptedException {

        AtomicInteger count = new AtomicInteger(0);
/*        for (int i = 0; i < 20; i++) {
            int reqOrder = i;
            CompletableFuture.runAsync(() -> {
                System.out.println(reqOrder + ": 요청, 시각: " + formatter.format(LocalDateTime.now()));
                ODsayTransitRouteResponse response = odsayApiClient.searchTransitRoute(
                        Coordinate.from(126.98708591399983, 37.56127528907461),
                        Coordinate.from(126.99023335682591, 37.55377929365595)
                );
                if (response instanceof ODsayRouteInvalidResponse) {
                    System.out.println(reqOrder + ": 실패");
                } else {
                    System.out.println(reqOrder + ": 성공");
                }
            });
        }*/
        scheduler.scheduleAtFixedRate(() -> {
            int reqOrder = count.incrementAndGet();

                CompletableFuture.runAsync(() -> {
                    System.out.println(reqOrder + ": 요청, 시각: " + formatter.format(LocalDateTime.now()));
                    ODsayTransitRouteResponse response = odsayApiClient.searchTransitRoute(
                            Coordinate.from(126.98708591399983, 37.56127528907461),
                            Coordinate.from(126.99023335682591, 37.55377929365595)
                    );
                    if (response instanceof ODsayRouteInvalidResponse) {
                        System.out.println(reqOrder + ": 실패");
                    } else {
                        System.out.println(reqOrder + ": 성공");
                    }
                });

        }, 0, 200, TimeUnit.MILLISECONDS);

        Thread.sleep(Duration.ofSeconds(2));


    }

}