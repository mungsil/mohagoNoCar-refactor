package com.example.mohago_nocar.course.application.dto;

public sealed interface EventHandleResult permits
        EventHandleSuccess,
        EventHandleFailure {

}
