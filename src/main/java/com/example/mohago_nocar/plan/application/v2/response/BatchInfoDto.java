package com.example.mohago_nocar.plan.application.v2.response;

import com.example.mohago_nocar.transit.infrastructure.queue.batch.TransitRouteBatchExecution;
import lombok.Builder;

@Builder
public record BatchInfoDto(
        String batchId,
        String userId,
        String status,
        Integer totalCount,
        Integer completedCount,
        Long createdAt,
        Long completedAt,
        String errorMessage
) {

    public static BatchInfoDto from(TransitRouteBatchExecution batch) {
        return BatchInfoDto.builder()
                .batchId(batch.getId())
                .userId(batch.getUserId().toString())
                .status(batch.getStatus().name())
                .totalCount(batch.getTotalCount())
                .completedCount(batch.getCompletedCount())
                .createdAt(batch.getCreatedAt())
                .completedAt(batch.getCompletedAt())
                .errorMessage(batch.getErrorMessage())
                .build();
    }

}
