package com.example.mohago_nocar.course.domain.model.course;

import com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent;
import com.example.mohago_nocar.global.common.domain.BaseEntity;
import com.example.mohago_nocar.global.common.domain.OutboxStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelCourseOptimizedEventOutbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private TravelCourseOptimizedEvent payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(nullable = true)
    private String failReason;

    public static TravelCourseOptimizedEventOutbox create(TravelCourseOptimizedEvent event) {
        return TravelCourseOptimizedEventOutbox.builder()
                .payload(event)
                .retryCount(0)
                .status(OutboxStatus.PENDING)
                .build();
    }

    @Builder
    private TravelCourseOptimizedEventOutbox(TravelCourseOptimizedEvent payload, OutboxStatus status, Integer retryCount) {
        this.payload = payload;
        this.status = status;
        this.retryCount = retryCount;
    }

    public boolean isFinalRetry() {
        return  3 <= retryCount;
    }

    public int incrementRetryCount() {
        return ++retryCount;
    }

    public void markAsPublished() {
        this.status = OutboxStatus.SENT;
    }

    public void markFailWithReason(OutboxStatus outboxStatus, Throwable throwable) {
        this.status = outboxStatus;
    }

}
