package com.example.mohago_nocar.course.application.route;

import com.example.mohago_nocar.course.application.spot.TravelSpotService;
import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpot;
import com.example.mohago_nocar.support.LocalIntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

class RouteStepFinderTest extends LocalIntegrationTestSupport {

    @Autowired
    private RouteFinder routeStepFinder;

    @Autowired
    private TravelSpotService travelSpotService;

/*    @Test
    @DisplayName("호출 순서대로 API 요청을 완료된다.")
    void shouldCompleteOneByOne() throws InterruptedException {
        //given
        long travelCourseId = 52L;
        List<TravelSpot> spots = travelSpotService.getByCourseId(travelCourseId);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            countDownLatch.await();
            executorService.submit(() -> {
                List<CompletableFuture<RouteStep>> futures = routeStepFinder.findRouteInTravelCourse(travelCourseId, spots);
                List<RouteStep> routeSteps = futures.stream().map(CompletableFuture::join).toList();
                System.out.println(finalI +"번째 요청 완료: " + routeSteps);
            });
        }

        countDownLatch.countDown();

        //when

        //then

    }*/

    @Test
    @DisplayName("호출 순서대로 API 요청을 완료한다.")
    void shouldCompleteOneByOne() throws InterruptedException {
        // given
        long travelCourseId = 25L;
        List<TravelSpot> spots = travelSpotService.getByCourseId(travelCourseId);
        System.out.println("spots size: " + spots.size());

        int requestCount = 1;
        System.out.println("total request count: " + requestCount * (spots.size() - 1));

        CountDownLatch latch = new CountDownLatch(requestCount);
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);

        // 예외 스택트레이스를 모아둘 리스트
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < requestCount; i++) {
            executorService.submit(() -> {
                try {
                    List<CompletableFuture<RouteStep>> futures =
                            routeStepFinder.findRouteInTravelCourse(travelCourseId, new ArrayList<>(spots));
                    List<RouteStep> routeSteps = new ArrayList<>();
                    for (CompletableFuture<RouteStep> future : futures) {
                        RouteStep join = future.join();
                        routeSteps.add(join);
                    }
                    System.out.println("응답: " + routeSteps);
                    System.out.println(Thread.currentThread() + "의 임무 완료 시각 :" + LocalDateTime.now());

                } catch (Exception e) {
                    // 예외를 리스트에 저장
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(120, TimeUnit.SECONDS);
        Thread.sleep(Duration.ofSeconds(50));
        executorService.shutdown();

        // 모든 예외 출력
        if (!exceptions.isEmpty()) {
            System.out.println("=== 발생한 예외 스택트레이스 ===");
            for (Throwable t : exceptions) {
                t.printStackTrace(System.out);
            }
        }

        assertThat(completed).isTrue();
    }


}