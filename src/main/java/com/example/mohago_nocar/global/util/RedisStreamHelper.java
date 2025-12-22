package com.example.mohago_nocar.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisStreamHelper {

    private final StringRedisTemplate redisTemplate;

    public RecordId addNewObjectRecord(String streamKey, String payload) {
        ObjectRecord<String, String> record = StreamRecords.newRecord().in(streamKey).ofObject(payload);
        return redisTemplate.opsForStream().add(record);
    }

    public PendingMessages getPendingMessages(
            String streamKeyName, String consumerGroupName, String consumerName, int maxScanNum, Range<Object> range) {
        return redisTemplate.opsForStream()
                .pending(
                        streamKeyName,
                        Consumer.from(consumerGroupName, consumerName),
                        range,
                        maxScanNum);
    }

    public PendingMessages getPendingMessages(
            String streamKeyName, String consumerGroupName, int maxScanNum, Range<Object> range) {
        return redisTemplate.opsForStream()
                .pending(
                        streamKeyName,
                        consumerGroupName,
                        range,
                        maxScanNum);
    }

    public void acknowledgeAndDelete(String streamKeyName, String consumerGroupName, RecordId[] recordIds) {
        redisTemplate.opsForStream().acknowledge(streamKeyName, consumerGroupName, recordIds);
        redisTemplate.opsForStream().delete(streamKeyName, recordIds);
    }

    public void acknowledgeAndDelete(String streamKeyName, String consumerGroupName, RecordId recordId) {
        redisTemplate.opsForStream().acknowledge(streamKeyName, consumerGroupName, recordId);
        redisTemplate.opsForStream().delete(streamKeyName, recordId);
    }

    public MapRecord<String, Object, Object> readMessage(String streamKeyName, RecordId recordId) {
        List<MapRecord<String, Object, Object>> range = redisTemplate.opsForStream()
                .range(streamKeyName, Range.just(recordId.getValue()));

        return range.stream()
                .findFirst()
                .orElse(null);
    }

}