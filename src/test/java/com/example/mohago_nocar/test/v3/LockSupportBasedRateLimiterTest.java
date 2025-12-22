package com.example.mohago_nocar.test.v3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LockSupportBasedRateLimiterTest {

    LockSupportBasedRateLimiter rateLimiter = new LockSupportBasedRateLimiter();

    @Test
    @DisplayName("")
    void test() {
        //given

        //when
        for (int i = 0; i < 5; i++) {
            rateLimiter.execute();
        }
        //then

    }

}