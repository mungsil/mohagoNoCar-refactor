package com.example.mohago_nocar.global.messaging;

public class DeadLetterProcessingException extends RuntimeException {

    public DeadLetterProcessingException(String message) {
        super(message);
    }

}
