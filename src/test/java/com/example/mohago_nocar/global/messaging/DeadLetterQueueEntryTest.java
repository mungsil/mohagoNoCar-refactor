package com.example.mohago_nocar.global.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.RecordId;

import java.time.Duration;

class DeadLetterQueueEntryTest {

    @Test
    void create() {
        PendingMessage pendingMessage = new PendingMessage(
                RecordId.of("1670000000-0"),
                Consumer.from("consumerGroup", "consumer"),               // Consumer 객체
                Duration.ofMillis(1000),                 // 마지막 전달 이후 경과 시간
                1L                                      // 총 전달 횟수
        );

        DeadLetterQueueEntry entry = DeadLetterQueueEntry.create(
                DeadLetterQueueEntryDto.from("Sdfdsf", pendingMessage, "ㄹㄴㅇㄹㅇ"));
        System.out.println(entry);
    }

}