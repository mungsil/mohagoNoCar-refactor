package com.example.mohago_nocar.global.messaging;

import com.example.mohago_nocar.course.infrastructure.stream.DeadLetterQueueEntryDto;
import com.example.mohago_nocar.global.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "dead_letter_queue")
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeadLetterQueueEntry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("DLQ 엔트리 ID")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("Redis Stream 키")
    private String streamKey;

    @Column(nullable = false, length = 100)
    @Comment("컨슈머 그룹 이름")
    private String consumerGroup;

    @Column(nullable = false, length = 100)
    @Comment("Stream entry ID")
    private String entryId;

    @Column(columnDefinition = "TEXT")
    @Comment("메시지 페이로드 (JSON)")
    private String payload;

    @Comment("예외 타입")
    private String exceptionType;

    @Column(columnDefinition = "TEXT")
    @Comment("에러 메시지")
    private String errorMessage;

    @Column(columnDefinition = "TEXT")
    @Comment("스택 트레이스")
    private String stackTrace;

    @Comment("전달 시도 횟수")
    private Long deliveryCount;

    @Column(length = 100)
    @Comment("마지막 처리 컨슈머")
    private String lastConsumer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 200)
    @Comment("처리 상태")
    private DLQStatus status;

    @Column
    @Comment("해결 완료 시간")
    private LocalDateTime resolvedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private DeadLetterQueueEntry(String streamKey, String consumerGroup, String entryId,
                                 String payload, String exceptionType, String errorMessage,
                                 String stackTrace, Long deliveryCount, String lastConsumer,
                                 DLQStatus status, LocalDateTime resolvedAt) {
        this.streamKey = streamKey;
        this.consumerGroup = consumerGroup;
        this.entryId = entryId;
        this.payload = payload;
        this.exceptionType = exceptionType;
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
        this.deliveryCount = deliveryCount;
        this.lastConsumer = lastConsumer;
        this.status = status;
        this.resolvedAt = resolvedAt;
    }

    public static DeadLetterQueueEntry create(DeadLetterQueueEntryDto dto) {
        DeadLetterQueueEntryBuilder builder = DeadLetterQueueEntry.builder()
                .streamKey(dto.getStreamName())
                .consumerGroup(dto.getGroupName())
                .entryId(dto.getId())
                .payload(dto.getPayload())
                .deliveryCount(dto.getDeliveryCount())
                .lastConsumer(dto.getConsumerName())
                .status(DLQStatus.NEW);

        if (dto.getThrowable() != null) {
            Throwable throwable = dto.getThrowable();
            return builder.errorMessage(throwable.getMessage())
                    .exceptionType(throwable.getClass().getSimpleName())
                    .stackTrace(throwable.getStackTrace().toString())
                    .build();
        } else {
            return builder.build();
        }

    }

    public void changeStatus(DLQStatus newStatus) {
        this.status = newStatus;

        if (newStatus == DLQStatus.RESOLVED) {
            this.resolvedAt = LocalDateTime.now();
        }
    }

    public void syncUpdateTime(LocalDateTime now) {
        this.setUpdatedAt(now);
    }

}