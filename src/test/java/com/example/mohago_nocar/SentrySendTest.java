package com.example.mohago_nocar;

import com.example.mohago_nocar.support.IntegrationTestSupport;
import io.sentry.Sentry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SentrySendTest extends IntegrationTestSupport {

    @Test
    @DisplayName("")
    void shouldSendAlert(){
        //given

        //when
        try {
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }

        //then

    }

}
