package com.example.mohago_nocar;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.support.IntegrationTestSupport;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.ODsayApiRateLimitedClient;
import com.example.mohago_nocar.transit.infrastructure.route.odsay.response.ODsayTransitRouteResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class OdsayThrottleTest extends IntegrationTestSupport {

/*    @Autowired
    private ODsayApiRateLimitedClient odsayApiClient;

    private RateLimiter rateLimiter = initializeRateLimiter().rateLimiter("테스트 속도 조절기");


    @Test
    @DisplayName("1초에 몇 번 형식인가?")
    void test() throws InterruptedException {
        //given

        //when
        for (int i = 0; i < 20; i++) {
            CompletableFuture.runAsync(() -> {
                ODsayTransitRouteResponse response = odsayApiClient.searchTransitRoute(
                        Coordinate.from(126.98708591399983, 37.56127528907461),
                        Coordinate.from(126.99023335682591, 37.55377929365595)
                );

                System.out.println(response);
            });
        }

        Thread.sleep(Duration.ofSeconds(10));
    }

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREAN);

    @Test
    @DisplayName("속도 제한 로직이 문제인지 확인한다")
    void testRateLimiter() {
        //given
        String currentThreadName = Thread.currentThread().getName();

        //when
        for (int i = 0; i < 15; i++) {
            if (rateLimiter.acquirePermission()) {
                System.out.println(formatter.format(LocalDateTime.now()) + ":" + currentThreadName);
            }
        }

        //then

    }

    ExecutorService executorService = Executors.newFixedThreadPool(10);
    AtomicInteger index = new AtomicInteger(0);

    @Test
    @DisplayName("토큰 버킷 알고리즘 동작을 확인한다.")
    void playWithTokenBucket() throws InterruptedException {
        //given
        Bandwidth limit = BandwidthBuilder.builder().capacity(1)
                .refillGreedy(2, Duration.ofSeconds(1))
                .build();

        LocalBucket bucket = Bucket.builder()
                .addLimit(limit)
                .build();

        Thread.sleep(100);
        //when
        for (int i = 0; i < 20; i++) {
            int requestOrder = i;
            CompletableFuture.runAsync(() -> {

                try {
                    bucket.asBlocking().consume(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println(
                        "[획득] order=" + requestOrder +
                                ", thread=" + Thread.currentThread().getName() +
                                ", time=" + formatter.format(LocalDateTime.now())
                );

            });
        }
        Thread.sleep(Duration.ofSeconds(10));

    }

    @Test
    @DisplayName("blocking consume는 공평한 대기(FIFO)를 보장하지 않는다")
    void playWithTokenBucket_unfairBlocking() throws InterruptedException {
        // given
        Bandwidth limit = BandwidthBuilder.builder()
                .capacity(1) // 의도적으로 1로 설정
                .refillIntervally(1, Duration.ofSeconds(1))
                .initialTokens(1)
                .build();

        LocalBucket bucket = Bucket.builder()
                .addLimit(limit)
                .build();

        int threadCount = 10;

        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        for (int i = 0; i < threadCount; i++) {
            int requestOrder = i;

            CompletableFuture.runAsync(() -> {
                try {
                    // 모든 스레드가 준비될 때까지 대기
                    readyLatch.countDown();
                    startLatch.await();

                    System.out.println(
                            "[요청] order=" + requestOrder +
                                    ", thread=" + Thread.currentThread().getName() +
                                    ", time=" + formatter.format(LocalDateTime.now())
                    );

                    bucket.asBlocking().consume(1);

                    System.out.println(
                            "[획득] order=" + requestOrder +
                                    ", thread=" + Thread.currentThread().getName() +
                                    ", time=" + formatter.format(LocalDateTime.now())
                    );

                } catch (InterruptedException e) {
                    System.out.println("인터럽트 발생: " + requestOrder);
                }
            }, executorService);
        }

        // 모든 스레드가 준비될 때까지 대기
        readyLatch.await();

        System.out.println("=== 모든 요청 스레드 준비 완료, 동시에 시작 ===");
        startLatch.countDown();

        Thread.sleep(Duration.ofSeconds(12));
    }


    private RateLimiterRegistry initializeRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(5)
                .timeoutDuration(Duration.ofSeconds(30))
                .build();

        return RateLimiterRegistry.of(config);
    }

    private static Bucket bucket;

    @BeforeAll
    public static void init() {
        // 200ms마다 1개의 토큰을 엄격하게 추가
        Bandwidth limit = BandwidthBuilder.builder().capacity(1)
                .refillGreedy(4, Duration.ofSeconds(1))
                .initialTokens(1)
                .build();

        bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Test
    @DisplayName("넷플리그 속도 제한 로직 대신 구글을 사용해본다")
    public void executeWithRateLimit() throws InterruptedException {
        String currentThreadName = Thread.currentThread().getName();

        Thread.sleep(Duration.ofSeconds(2));
        try {
            for (int i = 0; i < 10; i++) {
                bucket.asBlocking().consume(1); // 토큰을 얻을 때까지 블로킹
                System.out.println(formatter.format(LocalDateTime.now()) + ":" + currentThreadName);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }*/

}
