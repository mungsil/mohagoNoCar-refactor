package com.example.mohago_nocar.global.messaging;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.redis.connection.stream.PendingMessage;

@Getter
@ToString
public class DeadLetterQueueEntryDto {

    private String id;
    private String streamName;
    private String groupName;
    private String consumerName;
    private String payload;
    private Throwable throwable;

    public static DeadLetterQueueEntryDto of(
            String id, String streamName, String groupName, String consumerName, String payload, Throwable throwable) {
        return new DeadLetterQueueEntryDto(id, streamName, groupName, consumerName, payload, throwable);
    }

    public static DeadLetterQueueEntryDto from(String streamName, PendingMessage pendingMessage, String payload) {
        return new DeadLetterQueueEntryDto(
                pendingMessage.getId().toString(),
                streamName,
                pendingMessage.getGroupName(),
                pendingMessage.getConsumerName(),
                payload
                ,null
        );
    }

    @Builder(access = AccessLevel.PRIVATE)
    private DeadLetterQueueEntryDto(
            String id, String streamName, String groupName,
            String consumerName, String payload, Throwable throwable) {
        this.id = id;
        this.streamName = streamName;
        this.groupName = groupName;
        this.consumerName = consumerName;
        this.payload = payload;
        this.throwable = throwable;
    }

}
