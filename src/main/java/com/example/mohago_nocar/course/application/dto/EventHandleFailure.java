package com.example.mohago_nocar.course.application.dto;

public final class EventHandleFailure implements EventHandleResult {

    private Throwable throwable;

    public static EventHandleFailure create(Throwable throwable) {
        return new EventHandleFailure(throwable);
    }

    private EventHandleFailure(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable get() {
        return throwable;
    }
}
