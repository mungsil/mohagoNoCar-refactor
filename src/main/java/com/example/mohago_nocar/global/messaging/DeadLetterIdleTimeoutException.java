
package com.example.mohago_nocar.global.messaging;

public class DeadLetterIdleTimeoutException extends RuntimeException {

    public DeadLetterIdleTimeoutException(String message) {
        super(message);
    }

}
