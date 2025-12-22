package com.example.mohago_nocar.global.rateLimit;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 연속된 작업 실행 사이의 최소 시간 간격을 보장합니다.
 * 여러 스레드가 동시에 접근해도 thread-safe하게 rate limiting을 수행합니다.
 */
@Slf4j
public class IntervalRateLimiter implements RateLimiter {

    private long nextExecutionTimeNanos;

    private final long minIntervalNanos;
    private final ReentrantLock entryLock;

    public IntervalRateLimiter(long minIntervalMillis) {
        this.minIntervalNanos = TimeUnit.MILLISECONDS.toNanos(minIntervalMillis);
        this.nextExecutionTimeNanos = System.nanoTime();
        this.entryLock = new ReentrantLock();
    }

    /**
     * 다음 실행이 허용될 때까지 현재 스레드를 대기시킵니다.
     * Rate limit을 초과하지 않도록 필요한 시간만큼 blocking합니다.
     */
    @Override
    public void throttle() {
        entryLock.lock();
        try {
            long now = System.nanoTime();
            long waitTimeNanos = Math.max(0, nextExecutionTimeNanos - now);

            if (waitTimeNanos > 0) {
                LockSupport.parkNanos(waitTimeNanos);
            }

            this.nextExecutionTimeNanos = System.nanoTime() + minIntervalNanos;

        } finally {
            entryLock.unlock();
        }

    }

}
