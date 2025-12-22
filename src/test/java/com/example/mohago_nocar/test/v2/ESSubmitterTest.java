package com.example.mohago_nocar.test.v2;

import com.example.mohago_nocar.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

class ESSubmitterTest extends IntegrationTestSupport {

    @Autowired
    ESSubmitter esSubmitter;

    @Test
    @DisplayName("ExecutorService Task에서 예외를 던지면 ?된다!")
    void test() {
        esSubmitter.submit();
        esSubmitter.submit();
        esSubmitter.submit();

        sleep(3);
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(Duration.ofSeconds(seconds));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}