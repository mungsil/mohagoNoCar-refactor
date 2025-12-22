package com.example.mohago_nocar.course.infrastructure.course.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class TravelCourseOptimizedStreamRecoveryManager {

    private final StringRedisTemplate stringRedisTemplate;
    private final TravelCourseOptimizedMessageConsumer travelCourseOptimizedMessageConsumer;

    public void recovery(String streamKey, String consumerGroupName, String consumerName) {
        StreamOperations<String, Object, Object> streamOps = stringRedisTemplate.opsForStream();

        // 아래 코드 실행 시 라이브러리 버그로 인한 오류 발생
//        PendingMessages pendingMessages = streamOps.pending(streamKey, Consumer.from(consumerGroupName, consumerName));
//        long totalPendingMessages = pendingMessages.size();

        // 현재 단일 컨슈머이므로 그룹 내 모든 Pending Message 조회
        PendingMessagesSummary summary = streamOps.pending(streamKey, consumerGroupName);
        long totalPendingMessages = summary.getTotalPendingMessages();
        log.info("Pending messages in consumer group [{}]: {}", consumerGroupName, totalPendingMessages);

        StreamReadOptions options = StreamReadOptions.empty()
                .count(totalPendingMessages);

        Consumer consumer = Consumer.from(consumerGroupName, consumerName);

        List<ObjectRecord<String, String>> records = streamOps.read(
                        consumer,
                        options,
                        StreamOffset.fromStart(streamKey)).stream()
                .map(record -> {
                    RecordId id = record.getId();
                    Map<Object, Object> recordValue = record.getValue();
                    return ObjectRecord.create(streamKey, recordValue.get("payload").toString()).withId(id);
                })
                .toList();

        log.info("Starting recovery process for pending messages");
        for (ObjectRecord<String, String> record : records) {
            try {
                travelCourseOptimizedMessageConsumer.onMessage(record);
            } catch (Exception e) {
                log.error("Failed to reprocess pending message. RecordId={}", record.getId(), e);
            }

        }
    }

}