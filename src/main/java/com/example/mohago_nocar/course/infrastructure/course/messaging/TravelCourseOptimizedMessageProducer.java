package com.example.mohago_nocar.course.infrastructure.course.messaging;

import com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent;
import com.example.mohago_nocar.global.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class TravelCourseOptimizedMessageProducer implements TravelCourseOptimizedEventPublisher {

    private final String streamKey;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapperUtil objectMapperUtil;

    @Override
    public void publish(TravelCourseOptimizedEvent event) {
        String eventStr = objectMapperUtil.writeValue(event);

        ObjectRecord<String, String> record = StreamRecords
                .newRecord()
                .ofObject(eventStr)
                .withStreamKey(streamKey);

        stringRedisTemplate.opsForStream().add(record);
    }

}
