package com.example.mohago_nocar.transit.infrastructure.queue.producer;

import com.example.mohago_nocar.global.common.DeadMessageCreator;
import com.example.mohago_nocar.global.common.DeadSummaryMessage;
import com.example.mohago_nocar.global.util.ObjectMapperUtil;
import com.example.mohago_nocar.transit.infrastructure.queue.batch.TransitRouteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransitRouteDeadRequestProducer {

    @Value("${redis.streams.odsay.dlq}")
    private String streamKey;

    private final ObjectMapperUtil objectMapperUtil;
    private final DeadMessageCreator deadMessageCreator;
    private final StringRedisTemplate stringRedisTemplate;

    public void produce(TransitRouteRequest request, Exception ex) {
        // 아래는...서비스로...?
        DeadSummaryMessage deadMessage = deadMessageCreator.createSummaryMessage(
                objectMapperUtil.writeValue(request), ex, 0, 10);

        ObjectRecord<String, String> entry = StreamRecords.newRecord()
                .in(streamKey)
                .ofObject(objectMapperUtil.writeValue(deadMessage));

        stringRedisTemplate.opsForStream().add(entry);
    }


}
