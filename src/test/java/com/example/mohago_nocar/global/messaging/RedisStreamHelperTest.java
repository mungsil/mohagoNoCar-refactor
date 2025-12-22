package com.example.mohago_nocar.global.messaging;

import com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent;
import com.example.mohago_nocar.global.util.ObjectMapperUtil;
import com.example.mohago_nocar.global.util.RedisStreamHelper;
import com.example.mohago_nocar.support.LocalIntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.UUID;

class RedisStreamHelperTest extends LocalIntegrationTestSupport {

    @Autowired
    private ObjectMapperUtil objectMapper;

    @Autowired
    private RedisStreamHelper redisStreamHelper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("")
    void shouldAddStream(){
        //given
        String eventJson = objectMapper.writeValue(
                TravelCourseOptimizedEvent.of(1L, UUID.randomUUID())
        );

        //when
        ObjectRecord<String, String> record = StreamRecords.newRecord().in("mystream").ofObject(eventJson);
        RecordId recordId = redisTemplate.opsForStream().add(record);

        //then
        System.out.println("recordId: " + recordId);
        MapRecord<String, Object, Object> entries = redisStreamHelper.readMessage("mystream", RecordId.of(recordId.getValue()));
        System.out.println(entries.toString());
        System.out.println(entries.getValue());
    }

    @Test
    @DisplayName("레코드(엔트리)를 읽는다")
    void shouldReadRecord(){
        //given
        //when
        MapRecord<String, Object, Object> record = redisStreamHelper.readMessage("mystream", RecordId.of("1766322817723-0"));
        RecordId id = record.getId();
        Map<Object, Object> recordValue = record.getValue();
        ObjectRecord<String, String> objRecord = ObjectRecord.create("mystream", recordValue.toString()).withId(id);
        System.out.println(objRecord);
        String stream = objRecord.getStream();
        System.out.println(stream);

        //then
        System.out.println(record.toString());
        System.out.println(record.getValue());
        // 결과: MapBackedRecord{recordId=1761874798218-0, kvMap={user_name=k}}
    }

}