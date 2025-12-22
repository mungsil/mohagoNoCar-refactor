package com.example.mohago_nocar.global.messaging;

import com.example.mohago_nocar.support.LocalIntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeadLetterQueueServiceTest extends LocalIntegrationTestSupport {

    @Autowired
    DeadLetterQueueService deadLetterQueueService;

    @Test
    @DisplayName("Dead Letter를 저장한다.")
    void shouldSaveDeadLetter(){
        //given
        DeadLetterQueueEntryDto dto = DeadLetterQueueEntryDto.of("id", "streamName", "groupName", "consumerName",
                 "payload", new Throwable("테스트 중!"));

        //when
        deadLetterQueueService.save(dto);

        //then

    }

}