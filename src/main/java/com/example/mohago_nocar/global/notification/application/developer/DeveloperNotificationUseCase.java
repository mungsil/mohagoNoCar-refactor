package com.example.mohago_nocar.global.notification.application.developer;

public interface DeveloperNotificationUseCase {

    void sendNotification(String content);

    void sendNotification(String contentTitle, Throwable throwable);

}
