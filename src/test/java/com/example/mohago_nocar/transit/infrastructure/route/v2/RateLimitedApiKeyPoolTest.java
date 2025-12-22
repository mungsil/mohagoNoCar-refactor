package com.example.mohago_nocar.transit.infrastructure.route.v2;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RateLimitedApiKeyPoolTest {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREAN);


    @Test
    @DisplayName("")
    void shouldNotEnsureFirstInputFirstOut() throws InterruptedException {
        // given
        Bucket bucket = createBucket();
        ExecutorService executor = Executors.newFixedThreadPool(15);
        List<Integer> executionOrder = Collections.synchronizedList(new ArrayList<>());

        // when: 동시에 여러 스레드가 토큰 요청
        CountDownLatch latch = new CountDownLatch(1);
        for (int i = 1; i <= 15; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    latch.await(); // 동시에 시작
                    bucket.asBlocking().consume(1); // 토큰 획득 (대기 가능)
                    executionOrder.add(id); // 획득한 순서 기록
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        latch.countDown(); // 모든 스레드 동시에 시작
        executor.shutdown();
        executor.awaitTermination(15, TimeUnit.SECONDS);

        // then: 요청 순서(1,2,3,4,5)와 실제 획득 순서가 다를 수 있음
        System.out.println("Execution order: " + executionOrder);

        assertThat(executionOrder).isNotEqualTo(List.of(1, 2, 3, 4, 5));
    }

    private Bucket createBucket() {
        Bandwidth limit = BandwidthBuilder.builder().capacity(5)
                .refillIntervally(5, Duration.ofSeconds(1))
                .initialTokens(0)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

/*    @Test
    @DisplayName("요청 순서를 올바르게 카운팅한다")
    void shouldHaveRaceConditionWhenPlainInteger() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        //given
//        RateLimitedApiKeyPool pool = new RateLimitedApiKeyPool(fakeProperties(), () -> createBucket());

        RateLimitedApiKeyPool pool = new RateLimitedApiKeyPoolConfig().rateLimitedOdsayApiKeyPool(fakeProperties());
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<String>> futures = new ArrayList<>();

        //when
        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                String key = pool.acquireEncodedKey();
                System.out.println(formatter.format(LocalDateTime.now()) +
                        ":" + key);
                return key;
            }));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        //then
        int nextOrder = pool.getNextOrder();
        System.out.println("final order = " + nextOrder);
    }*/

    @Test
    @DisplayName("")
    void shouldGetAndIncrement(){
        //given
        AtomicInteger atomicInteger = new AtomicInteger();

        //when
        for (int i = 0; i < 20; i++) {
            int result = atomicInteger.getAndIncrement() % 2;
            System.out.println(i + "번째 요청의 결과:" + result);
        }

        //then
    }

    private Bucket mockBucket() {
        Bandwidth limit = BandwidthBuilder.builder()
                .capacity(Long.MAX_VALUE)
                .refillIntervally(1, Duration.ofNanos(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

}