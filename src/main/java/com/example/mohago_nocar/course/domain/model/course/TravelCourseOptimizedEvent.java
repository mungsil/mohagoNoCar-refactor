package com.example.mohago_nocar.course.domain.model.course;

import com.example.mohago_nocar.global.common.domain.BaseEntity;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelCourseOptimizedEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventProcessStatus status;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(nullable = true)
    private String failReason;

    public static TravelCourseOptimizedEvent create(com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent event) {
        return TravelCourseOptimizedEvent.builder()
                .payload(event)
                .retryCount(0)
                .status(EventProcessStatus.PENDING)
                .build();
    }

    @Builder
    private TravelCourseOptimizedEvent(com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent payload, EventProcessStatus status, Integer retryCount) {
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
        this.status = EventProcessStatus.SENT;
    }

    public void markFailWithReason(EventProcessStatus eventProcessStatus, Throwable throwable) {
        this.status = eventProcessStatus;
    }

}
