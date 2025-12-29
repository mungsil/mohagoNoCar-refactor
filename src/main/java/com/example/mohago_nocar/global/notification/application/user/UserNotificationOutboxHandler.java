package com.example.mohago_nocar.global.notification.application.user;

import com.example.mohago_nocar.global.notification.domain.UserNotificationMessageOutbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserNotificationOutboxHandler {

    private final UserNotificationOutboxService outboxService;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @Scheduled(fixedDelay = 1000)
    public void handle() {
        long handleStartTime = System.nanoTime();

        List<UserNotificationMessageOutbox> unpublishedList = outboxService.findUnpublished(10);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (UserNotificationMessageOutbox unpublished : unpublishedList) {
            CompletableFuture<Void> future =
                    CompletableFuture.runAsync(() -> {
                                outboxService.publish(unpublished);
                                outboxService.markAsPublished(unpublished);
                            }, executorService)
                            .exceptionally(throwable -> {
                                outboxService.processFailure(unpublished, throwable);
                                return null;
                            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long handleEndTime = System.nanoTime();
        log.info("Handle completed in {} ms", TimeUnit.NANOSECONDS.toMillis(handleEndTime - handleStartTime));
    }

}
