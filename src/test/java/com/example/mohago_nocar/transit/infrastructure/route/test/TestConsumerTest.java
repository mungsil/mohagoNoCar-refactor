package com.example.mohago_nocar.transit.infrastructure.route.test;

import com.example.mohago_nocar.support.IntegrationTestSupport;

class TestConsumerTest extends IntegrationTestSupport {

/*
    @Autowired
    TestProducer testProducer;

    @Autowired
    TestConsumer testConsumer;

    @Test
    @DisplayName("기본 설정 스레드 동작을 확인한다")
    void test() throws InterruptedException {
        //given
        int requestThreads = 3;
        ExecutorService executor = Executors.newFixedThreadPool(requestThreads);
        CountDownLatch countDownLatch = new CountDownLatch(requestThreads);

        // when
        for (int i = 1; i <= requestThreads; i++) {
            final int index = i;

            executor.submit(() -> {
                try {
                    testProducer.produce(index + "번째 메시지임둥");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        //then
        Thread.sleep(Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("스트림 리스너 컨테이너에 executor를 지정하는 경우 동작을 확인한다")
    void test2() throws InterruptedException {
        //given

        //when

        //then

    }

    @Test
    @DisplayName("예외 객체 메서드를 사용해봐요")
    void test3(){
        //given
        try {
            throw new CustomException(GlobalStatus.ENTITY_NOT_FOUND);
        } catch (CustomException e) {
            System.out.println("getMessage:" + e.getMessage());
            System.out.println("getCause:" + e.getCause());
            List<String> topStackTrace = getTopStackTrace(e, 10);
            for (String s : topStackTrace) {
                System.out.println(s);
            }
        }

        //when

        //then

    }

    @Test
    @DisplayName("")
    void test4() throws InterruptedException {
        //given

        //when
        for (int i = 0; i < 10; i++) {
            testProducer.produce(i + "번째 메시지임둥");
        }

        //then
        Thread.sleep(Duration.ofSeconds(1));
        testConsumer.destroy();
        Thread.sleep(Duration.ofSeconds(10));
    }


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREAN);

    @Test
//    @DisplayName("New가 있어도 PEL부터 읽는다는게 사실인지 확인한다 -> 구라임")
    @DisplayName("Completable Future timeout 기능 확인해봐요")
    void test6(){
        //given
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(
                () -> {
                    try {
                        Thread.sleep(Duration.ofSeconds(3));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("안녕? ");
                    return "안녕? ";
                });

        CompletableFuture<String> future2 = future1
                .thenApply(s -> {
                    try {
                        Thread.sleep(Duration.ofSeconds(1));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("응 ~ 안녕");
                    return s + "응 ~ 안녕";
                })
                .thenApply(s -> {
                    try {
                        Thread.sleep(Duration.ofSeconds(3));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return s;
                });

        //when

        try {
            String result = future2.orTimeout(2, SECONDS).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


        //then

    }

    @Test
    @DisplayName("")
    void test5(){
        //given

        //when
        for (int i = 0; i < 5; i++) {
            System.out.println(formatter.format(LocalDateTime.now()));
            LockSupport.parkNanos(SECONDS.toNanos(1));
            System.out.println(formatter.format(LocalDateTime.now()));

        }

        //then

    }

    private static List<String> getTopStackTrace(Throwable ex, int limit) {
        return Arrays.stream(ex.getStackTrace())
                .limit(limit)
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }
*/

}