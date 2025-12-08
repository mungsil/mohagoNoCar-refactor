package com.example.mohago_nocar.course.infrastructure.stream;

import com.example.mohago_nocar.global.messaging.DeadLetterQueueService;
import com.example.mohago_nocar.global.messaging.RedisStreamHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class DeadMessageProcessor {

    private final RedisStreamHelper redisStreamHelper;
    private final DeadLetterQueueService deadLetterQueueService;

    private final String streamKey;
    private final String consumerGroup;

    @Transactional
    public void process(List<PendingMessage> deadMessages) {
        log.warn("Processing dead messages: {}", deadMessages);
        List<DeadLetterQueueEntryDto> dtos = deadMessages.stream()
                .map(pm -> {
                    MapRecord<String, Object, Object> readMessage = redisStreamHelper
                            .readMessage(streamKey, pm.getId());

                    return DeadLetterQueueEntryDto.from(streamKey, pm, readMessage.getValue().toString());
                })
                .toList();

        deadLetterQueueService.saveAll(dtos);

        RecordId[] recordIds = deadMessages.stream()
                .map(PendingMessage::getId)
                .toArray(RecordId[]::new);

        try {
            redisStreamHelper.acknowledgeAndDelete(streamKey, consumerGroup, recordIds);
        } catch (RedisSystemException e) {
            log.error("Failed to acknowledge messages By RedisSystemException : {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to acknowledge messages By Exception : {}", e.getMessage(), e);
            throw e;
        }

        log.debug("Successfully processed dead letter messages, size: {}", deadMessages.size());
    }

}
