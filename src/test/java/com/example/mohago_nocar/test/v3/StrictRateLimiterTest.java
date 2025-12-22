package com.example.mohago_nocar.test.v3;

import com.example.mohago_nocar.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class StrictRateLimiterTest extends IntegrationTestSupport {

    @Autowired
    private StrictRateLimiter strictRateLimiter;

    @Test
    @DisplayName("")
    void test(){
        //given

        //when
        for (int i = 0; i < 5; i++) {
            strictRateLimiter.executeWithRateLimit();
        }

        //then

    }

}