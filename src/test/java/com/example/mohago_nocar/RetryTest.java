package com.example.mohago_nocar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class RetryTest {

/*    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    Logger log = LoggerFactory.getLogger(RetryTest.class);

    public Retry init() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .retryOnException(e -> !(e instanceof RuntimeException))
                .intervalFunction(IntervalFunction.ofExponentialBackoff(1000, 2.0)) // 1초, 2배씩 증가
                .build();

        Retry retry = Retry.of("transitApiRetry", retryConfig);
        retry.getEventPublisher()
                .onRetry(event -> log.warn("재시도 발생: {}번째 시도, 원인={}",
                        event.getNumberOfRetryAttempts(),
                        event.getLastThrowable().toString()))
                .onError(throwable -> log.error("재시도 실패: 원인={}",
                        throwable.getLastThrowable().toString()));

        return retry;
    }

    @Test
    @DisplayName("DecorateCallable을 사용해봐요")
    void testDecorateCallable(){
        //given
        Retry retry = init();

        //when
        Callable<Void> callable = Retry.decorateCallable(retry,
                () -> {
                    blockAndPrintAndThrowCheckedException("API 응답 대기 중...");
                    return null;
                });

        //then
        try {
            callable.call();
        } catch (Exception e) {
            System.out.println("API 호출에 실패했어요 ㅜㅜ");
            throw new RuntimeException(e);
        }

    }

    private void blockAndPrintAndThrowCheckedException(String message) throws IOException {
        try {
            System.out.println(Thread.currentThread().getName() + ": " + "키 획득 대기를 시작합니다");
            Thread.sleep(Duration.ofSeconds(1)); // 여기까지 호출자 스레드가 수행(처음에만), equal acquire key
            System.out.println(Thread.currentThread().getName() + ":" + message);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        throw new IOException("아이쿠! 실패!");
    }

    @Test
    @DisplayName("CompletableFuture를 재시도 해요")
    void testRetry() {
        //given
        Retry retry = init();

        //when
        Supplier<CompletionStage<Void>> stageSupplier = Retry.decorateCompletionStage(
                retry,
                scheduler,
                () -> blockAndPrint("API 응답 대기 중...")
        );

        CompletionStage<Void> stage = stageSupplier.get();
        CompletableFuture<Void> future = stage.thenRun(() -> System.out.println("API 호출 완료"))
                .toCompletableFuture();
        try {
            future.join();
        } catch (Exception e) {
            System.out.println("API 호출에 실패했어요 ㅜㅜ");
        }

        //then

    }

    CompletableFuture<Void> blockAndPrint(String printMsg) {
        try {
            System.out.println(Thread.currentThread().getName() + ": " + "키 획득 대기를 시작합니다");
            Thread.sleep(Duration.ofSeconds(3)); // 여기까지 호출자 스레드가 수행(처음에만), equal acquire key
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return CompletableFuture.runAsync(
                () -> {
                    System.out.println(Thread.currentThread().getName() + ":" + printMsg);
                    throw new RuntimeException("API 호출 실패");
                }); // equal api call
    }*/

}
