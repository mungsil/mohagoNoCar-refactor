package com.example.mohago_nocar.course.infrastructure.course.messaging;

import com.example.mohago_nocar.global.notification.application.developer.DeveloperNotificationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class LongPendingMessageHandler {

    private final LongPendingMessageReader longPendingMessageReader;
    private final DeveloperNotificationUseCase developerNotificationUseCase;

    private static final int FIXED_RATE_IN_MILS = 60_000; // 1분

    @Scheduled(fixedRate = FIXED_RATE_IN_MILS)
    public void process() {
        List<PendingMessage> pendingMessages = longPendingMessageReader.read();
        developerNotificationUseCase.sendNotification(
                "Long Pending 메시지가 " + pendingMessages.size() + "개 있습니다.");
    }

}
