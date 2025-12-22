package com.example.mohago_nocar.global.rateLimit;

import java.util.concurrent.TimeUnit;

public interface RateLimiter {

    void throttle();

    static RateLimiter create(long minIntervalMillis) {
        return new IntervalRateLimiter(minIntervalMillis);
    }

}
