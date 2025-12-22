package com.example.mohago_nocar.global.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Result {
    SUCCESS(true), FAILURE(false);

    private final boolean bool;

    public boolean isSuccess() {
        return bool;
    }

}
