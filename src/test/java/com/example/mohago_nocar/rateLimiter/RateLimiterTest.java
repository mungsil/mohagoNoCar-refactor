package com.example.mohago_nocar.rateLimiter;

import com.example.mohago_nocar.global.rateLimit.IntervalRateLimiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

class RateLimiterTest {

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.KOREAN);

    @Test
    @DisplayName("rate limit test")
    void shouldRateLimit() throws InterruptedException {
        //given
        IntervalRateLimiter rateLimiter = new IntervalRateLimiter(200);

        //when
        for (int i = 0; i < 5; i++) {
            final int order = i;
            CompletableFuture.runAsync(() -> {
                rateLimiter.throttle();
                System.out.println(order + "요청 받았습니다! : " + LocalDateTime.now().format(formatter));
            });

        }

        Thread.sleep(Duration.ofMillis(300));

        for (int i = 6; i < 10; i++) {
            final int order = i;
            CompletableFuture.runAsync(() -> {
                rateLimiter.throttle();
                System.out.println(order + "요청 받았습니다! : " + LocalDateTime.now().format(formatter));
            });

        }

        //then
        Thread.sleep(Duration.ofSeconds(3));
    }

}