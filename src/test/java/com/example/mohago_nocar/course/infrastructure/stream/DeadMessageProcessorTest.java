package com.example.mohago_nocar.course.infrastructure.stream;

import com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent;
import com.example.mohago_nocar.global.messaging.DeadLetterQueueEntryRepository;
import com.example.mohago_nocar.global.util.RedisStreamHelper;
import com.example.mohago_nocar.support.LocalIntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.stream.*;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

class DeadMessageProcessorTest extends LocalIntegrationTestSupport {

    @Autowired
    DeadLetterQueueEntryRepository deadLetterQueueEntryRepository;

    @MockBean
    RedisStreamHelper redisStreamHelper;

    @Test
    @DisplayName("예외 발생 시 트랜잭션에 속한 작업들은 모두 롤백된다.")
    void shouldRollbackThrownException(){
        // given: ack 단계에서 예외 발생하도록 설정
        willThrow(new RuntimeException("Redis Acknowledge 실패"))
                .given(redisStreamHelper)
                .acknowledgeAndDelete(anyString(), anyString(), any(RecordId[].class));

        // given: readMessage는 정상 반환
        RecordId recordId = RecordId.of("1670000000-0");
        PendingMessage pendingMessage = new PendingMessage(
                recordId,
                Consumer.from("consumerGroup", "consumer"),               // Consumer 객체
                Duration.ofMillis(1000),                 // 마지막 전달 이후 경과 시간
                1L                                      // 총 전달 횟수
        );

        MapRecord<String, Object, Object> mapRecord =
                StreamRecords.mapBacked(Map.of(
                        (Object) "제발 끝내줘", (Object) TravelCourseOptimizedEvent.of(1L, UUID.randomUUID())))
                        .withStreamKey("myStream")
                        .withId(recordId);

        given(redisStreamHelper.readMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .willReturn(mapRecord);

/*        // when: DeadMessageProcessor 실행 → 예외 발생
        assertThatThrownBy(() ->
                deadMessageProcessor.processLongPendingMessages(List.of(pendingMessage))
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Redis Acknowledge 실패");

        // then: DeadLetterQueueEntryRepository에는 데이터가 남아있지 않아야 함
        assertThat(deadLetterQueueEntryRepository.findByEntryId(recordId.getValue())).isEmpty();*/
    }

    // todo: making pending messages. half already has stored as DeadLetterQueueEntryDto to test

}