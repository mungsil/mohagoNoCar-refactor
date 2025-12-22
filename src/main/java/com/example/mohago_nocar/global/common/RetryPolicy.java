package com.example.mohago_nocar.global.common;

public interface RetryPolicy {

    boolean isRetryable(Throwable throwable);

}
