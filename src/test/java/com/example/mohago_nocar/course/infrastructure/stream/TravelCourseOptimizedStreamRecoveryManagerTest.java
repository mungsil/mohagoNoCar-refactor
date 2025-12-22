package com.example.mohago_nocar.course.infrastructure.stream;

import com.example.mohago_nocar.course.infrastructure.course.messaging.TravelCourseOptimizedStreamRecoveryManager;
import com.example.mohago_nocar.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

class TravelCourseOptimizedStreamRecoveryManagerTest extends IntegrationTestSupport {

    @Value("${redis.streams.travel-spot.main}")
    private String streamKey;

    private static final String CONSUMER_GROUP = "processors";
    private static final String CONSUMER_1 = "processor-1";

    @Autowired
    private TravelCourseOptimizedStreamRecoveryManager recoveryManager;

    @Test
    @DisplayName("동작 테스트")
    void test() {
        //given
        // pel에 엔트리 생성
        // recovery
        // delivery count, idle time check

        //when
        recoveryManager.recovery(streamKey, CONSUMER_GROUP, CONSUMER_1);

        //then

    }

}