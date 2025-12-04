package com.example.mohago_nocar.transit.infrastructure.queue.batch;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.*;

/**
 * 배치 작업의 상태를 기록합니다.
 */
// todo plan id 기록
@RedisHash(value = "batch")
@Builder(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class TransitRouteBatchExecution {

    @Id
    private String id;

    private UUID userId;

    private Long planId;

    private BatchStatus status;

    private Integer totalCount;

    private String completedSequences; // todo

    private Boolean isDelivered;

    private Long createdAt;

    private Long completedAt;

    // todo 삭제
    private String errorMessage;

    // todo synch가 필요한가? -> 루아스크립트로 변경
    public synchronized void fail(Exception exception) {
        status = BatchStatus.FAILED;
        errorMessage = exception.getMessage();
    }

    public static TransitRouteBatchExecution createInitial(int totalCount, UUID userId, Long planId) {
        String batchId = UUID.randomUUID().toString();

        return TransitRouteBatchExecution.builder()
                .id(batchId)
                .userId(userId)
                .planId(planId)
                .status(BatchStatus.PENDING)
                .totalCount(totalCount)
                .completedSequences(null)
                .isDelivered(false)
                .createdAt(Instant.now().toEpochMilli())
                .build();
    }

    public void completeDeliver() {
        this.isDelivered = true;
    }

    public boolean isAlreadyDelivered() {
        return isDelivered;
    }

    public int getCompletedCount() {
        return this.getCompletedSequences().split(",").length;
    }

/*    public String addCompletedSequence(String sequence) {
        if (completedSequences == null) {
            this.completedSequences = sequence;
        } else {
            this.completedSequences += "," + sequence;
        }

        return this.completedSequences;
    }*/

}


