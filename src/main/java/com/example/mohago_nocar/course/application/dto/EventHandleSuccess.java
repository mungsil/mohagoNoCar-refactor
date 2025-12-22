package com.example.mohago_nocar.course.application.dto;

import java.util.concurrent.CompletableFuture;

public final class EventHandleSuccess implements EventHandleResult {

    private CompletableFuture<Void> futureResult;

    public static EventHandleSuccess create(CompletableFuture<Void> futureResult) {
        return new EventHandleSuccess(futureResult);
    }

    private EventHandleSuccess(CompletableFuture<Void> futureResult) {
        this.futureResult = futureResult;
    }

    public CompletableFuture<Void> get() {
        return futureResult;
    }

}
