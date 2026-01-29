package com.example.mohago_nocar.course.domain.model.course;

import com.example.mohago_nocar.global.common.domain.BaseEntity;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.global.util.StackTraceExtractor;
import com.example.mohago_nocar.transit.infrastructure.error.exception.ODsayRouteException;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import jakarta.persistence.*;
import lombok.*;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseOptimizedEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long travelCourseId;

    private UUID anonymousUserId; // 참조용 필드

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventProcessStatus status;

    public static CourseOptimizedEvent create(AnonymousUser user, TravelCourse course) {
        return CourseOptimizedEvent.builder()
                .anonymousUserId(user.getId())
                .travelCourseId(course.getId())
                .status(EventProcessStatus.CREATED)
                .build();
    }

    @Builder
    private CourseOptimizedEvent(Long travelCourseId, UUID anonymousUserId, EventProcessStatus status) {
        this.travelCourseId = travelCourseId;
        this.anonymousUserId = anonymousUserId;
        this.status = status;
    }

    public CourseOptimizedEventConsume consumeSuccess() {
        this.status = EventProcessStatus.SUCCESS;

        return CourseOptimizedEventConsume.success(this);
    }

    public CourseOptimizedEventConsume consumeFailure(Exception ex, StackTraceExtractor stackTraceExtractor) {
        if (isRetryable(ex)) {
            this.status = EventProcessStatus.RETRYABLE_FAIL;
        } else {
            this.status = EventProcessStatus.FATAL_FAIL;
        }

        return CourseOptimizedEventConsume.failWithDetail(this, stackTraceExtractor.extractStackTrace(ex, 10));
    }

    private boolean isRetryable(Exception exception) {
        if (exception instanceof SocketTimeoutException ||
                exception instanceof ConnectException ||
                exception instanceof HttpTimeoutException) {
            return true;
        }

        if (exception instanceof ODsayRouteException oDsayRouteException) {
            return oDsayRouteException.getErrorCode().isTooManyRequests() ||
                    oDsayRouteException.getErrorCode().isServerError();
        }

        return false;
    }

    public TravelCourseCompletionMessage getCompletionNotificationMsg() {
        return switch (this.status) {
            case FATAL_FAIL -> null;
            case SUCCESS ->  TravelCourseCompletionMessage.SUCCESS;
            default -> throw new IllegalStateException("여행 코스 설계 완료 알림 메시지를 작성할 수 없는 상태입니다. status: " + this.status);
        };
    }
}
