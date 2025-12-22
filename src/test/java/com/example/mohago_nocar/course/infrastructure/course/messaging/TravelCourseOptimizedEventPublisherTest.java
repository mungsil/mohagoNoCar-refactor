package com.example.mohago_nocar.course.infrastructure.course.messaging;

import com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent;
import com.example.mohago_nocar.support.LocalIntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TravelCourseOptimizedEventPublisherTest extends LocalIntegrationTestSupport {

    @Autowired
    private TravelCourseOptimizedEventPublisher publisher;

    @Test
    @DisplayName("")
    void shouldPublish() throws InterruptedException {
        //given //when
        for (int i = 0; i < 5; i++) {
            publisher.publish(TravelCourseOptimizedEvent.of(61L, UUID.fromString("96ed22d6-ffd9-41c5-931a-d4a662e5054a")));
        }

        //then
        Thread.sleep(Duration.ofSeconds(10));
    }

}