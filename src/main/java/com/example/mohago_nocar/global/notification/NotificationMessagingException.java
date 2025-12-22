package com.example.mohago_nocar.global.notification;

public class NotificationMessagingException extends RuntimeException {

    public NotificationMessagingException(String message) {
        super(message);
    }

  public NotificationMessagingException(String message, Exception exception) {
      super(message, exception);
  }

}
