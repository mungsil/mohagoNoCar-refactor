package com.example.mohago_nocar.course.infrastructure.course.messaging;

import com.example.mohago_nocar.global.util.RedisStreamHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class LongPendingMessageReader {

    private final String streamKeyName;
    private final String consumerGroupName;
    private final String consumerName;
    private final int maxScanNum;
    private final long idleTimeThresholdMills;

    private final RedisStreamHelper redisStreamHelper;

    public List<PendingMessage> read() {
        log.debug("Starting long pending message scan...");
        PendingMessages pendingMessages = redisStreamHelper.getPendingMessages(
                streamKeyName, consumerGroupName, consumerName, maxScanNum, Range.unbounded());

        if (pendingMessages.isEmpty()) {
            log.debug("No pending messages found");
            return List.of();
        }

        return pendingMessages.stream()
                .filter(msg -> msg.getElapsedTimeSinceLastDelivery().toMillis() > idleTimeThresholdMills)
                .toList();
    }

}
