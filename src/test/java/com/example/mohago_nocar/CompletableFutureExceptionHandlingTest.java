package com.example.mohago_nocar;

import com.example.mohago_nocar.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CompletableFutureExceptionHandlingTest extends IntegrationTestSupport {

    private static ExecutorService executor;

    @BeforeAll
    static void setup() {
        // 테스트용 고정 스레드풀(동시성 제어를 위해)
        executor = Executors.newFixedThreadPool(4);
    }

    @AfterAll
    static void tearDown() {
        executor.shutdownNow();
    }

    @Test
    @DisplayName("타임아웃의 적용 범위를 관찰한다")
    void play_timeout(){
        //given

        //when
        // 전체 타임 아웃
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    System.out.println("API call");
                    try {
                        Thread.sleep(Duration.ofSeconds(5));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, executor)
                .orTimeout(3, TimeUnit.SECONDS) // 여기서 안걸림
                .exceptionally(throwable -> {
                    System.out.println("API call이 오래 걸림");
                    return null;
                })
                .thenRun(() -> {
                    System.out.println("Sending notify...");
                    try {
                        Thread.sleep(Duration.ofSeconds(8));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orTimeout(10, TimeUnit.SECONDS);

        //then
        future.join();
    }

    @Test
    @DisplayName("체이닝된 CF 중 중간 CF가 예외를 던질 시 동작을 관찰한다")
    void play_when_the_middle_future_throw_ex(){
        //given
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> System.out.println("첫 번째 CF입니다. 1억 정도?! 받았어요."))
                .thenRun(() -> System.out.println("두 번째 CF입니다. 2억 정도?! 받았어요."))
                .thenRun(() -> {
                    System.out.println("BOOOMMM!!!");
                    throw new RuntimeException("BBBBOOOOOOMMMMM~");
                })
                .thenRun(() -> {
                    System.out.println("BOOOMMM!!! BOOOMMM!!!");
                    throw new RuntimeException("BBBBOOOOOOMMMMM~ BBBBOOOOOOMMMMM~");
                })
                .thenRun(() -> System.out.println("마지막 CF입니다. 100억 정도 받았어요."))
                .exceptionally(throwable -> {
                    System.out.println("CF 촬영 중 이슈가 발생했어요. 위약금 1000억입니다.");
                    return null;
                });
        //when
        try {
            future.get();
        } catch (InterruptedException e) {
            System.out.println("InterruptedException");
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            System.out.println("ExecutionException");
            throw new RuntimeException(e);
        }

        //then

    }

    // 1) exceptionally()로 예외 복구
    @Test
    void exceptionally_recoversFromException_and_chainContinues() {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
                    throw new IllegalStateException("boom");
                }, executor)
                // 예외 발생시 대체값 반환
                .exceptionally(ex -> {
                    // 확인용: ex는 CompletionException 또는 직접 던진 예외의 래핑일 수 있음
                    return "default";
                })
                .thenApply(s -> s + "-continued");

        assertThat(cf.join()).isEqualTo("default-continued");
    }

    // 2) handle()은 성공/실패 둘 다 처리 가능
    @Test
    void handle_receivesResultOrException_and_canReturnDifferentValue() {
        CompletableFuture<Object> cf = CompletableFuture.supplyAsync(() -> "ok", executor)
                // 다음 단계에서 고의로 예외 발생
                .thenApply(s -> {
                    throw new RuntimeException("fail-in-thenApply");
                })
                // handle은 성공/실패 모두 실행됨
                .handle((res, ex) -> {
                    if (ex != null) {
                        // 예외가 발생한 경우 복구값 반환
                        return "handled-recovery";
                    } else {
                        return res;
                    }
                });

        assertThat(cf.join()).isEqualTo("handled-recovery");
    }

    // 3) whenComplete는 결과를 관찰만 하고 원래 결과를 넘겨준다
    @Test
    void whenComplete_observesOutcome_but_doesNotChangeResult_unlessThrows() {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> "good", executor)
                .whenComplete((res, ex) -> {
                    // 관찰용 작업(로깅 등). 원래 결과를 변경하지 않음.
                    assertThat(ex).isNull();
                    assertThat(res).isEqualTo("good");
                });

        // whenComplete는 원본 결과를 그대로 전달
        assertThat(cf.join()).isEqualTo("good");

        // 만약 whenComplete 안에서 예외를 던지면 그 예외가 전파된다.
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> "x", executor)
                .whenComplete((res, ex) -> {
                    throw new IllegalArgumentException("observer-threw");
                });

        assertThatThrownBy(cf2::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasRootCauseMessage("observer-threw");
    }

    // 4) 조합(thenCompose)된 단계에서 예외가 발생했을 때의 처리와 복구
    @Test
    void thenCompose_exceptionInComposedStage_canBeRecoveredWithExceptionally() {
        CompletableFuture<String> initial = CompletableFuture.supplyAsync(() -> "start", executor);

        CompletableFuture<String> pipeline = initial.thenCompose(s ->
                        // 내부에서 exception 발생
                        CompletableFuture.supplyAsync(() -> {
                            throw new IllegalStateException("compose-boom");
                        }, executor)
                )
                // compose에서 발생한 예외를 여기서 복구
                .exceptionally(ex -> "composed-default")
                .thenApply(s -> s + "-after");

        assertThat(pipeline.join()).isEqualTo("composed-default-after");
    }

    // 5) join()/get() 시 예외 래핑(CompletionException/ExecutionException) 확인
    @Test
    void join_wrapsCauseInCompletionException_whenStageFailed() {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            throw new UnsupportedOperationException("underlying");
        }, executor);

        assertThatThrownBy(cf::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("underlying");
    }

}
