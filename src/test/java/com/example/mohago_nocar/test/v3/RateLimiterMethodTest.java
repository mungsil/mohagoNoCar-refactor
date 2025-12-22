package com.example.mohago_nocar.test.v3;

import com.example.mohago_nocar.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterMethodTest extends IntegrationTestSupport {

    @Autowired
    private RateLimiterMethod rateLimiterMethod;

    @Test
    @DisplayName("두 메서드 동작이 동일함을 확인한다.")
    void test() throws InterruptedException {
        //given

        //when
        for (int i = 0; i < 5; i++) {
            rateLimiterMethod.experimentalRateLimitMethod(i);
        }

        //then
        Thread.sleep(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("")
    void test2() throws InterruptedException {
        //given

        //when
        for (int i = 0; i < 5; i++) {
            rateLimiterMethod.print();
        }

        //then
        Thread.sleep(Duration.ofSeconds(5));

    }

}