package com.example.mohago_nocar.global.notification.domain;

import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.global.common.domain.BaseEntity;
import com.example.mohago_nocar.global.notification.application.user.UserNotificationDto;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserNotificationMessageOutbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private UUID userId;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb", nullable = true)
    private Map<String, String> customData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventProcessStatus status;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(nullable = true)
    private String failReason;

    public static UserNotificationMessageOutbox of(
            EventProcessStatus status,
            Integer retryCount,
            String title,
            String body,
            UUID userId,
            Map<String, String> customData
    ) {
        return UserNotificationMessageOutbox.builder()
                .status(status)
                .retryCount(retryCount)
                .title(title)
                .body(body)
                .userId(userId)
                .customData(customData)
                .build();
    }

    public static UserNotificationMessageOutbox from(UserNotificationDto dto) {
        return UserNotificationMessageOutbox.of(
                EventProcessStatus.PENDING,
                0,
                dto.getTitle(),
                dto.getBody(),
                dto.getUserId(),
                dto.getCustomData()
        );
    }

    @Builder
    private UserNotificationMessageOutbox(
            EventProcessStatus status,
            Integer retryCount,
            String title,
            String body,
            UUID userId,
            Map<String, String> customData,
            String failReason
    ) {
        this.status = status;
        this.retryCount = retryCount;
        this.title = title;
        this.body = body;
        this.userId = userId;
        this.customData = customData;
        this.failReason = failReason;
    }

    public boolean isFinalRetry() {
        return 3 <= retryCount;
    }

    public void markAsPublished() {
        this.status = EventProcessStatus.SENT;
    }

    public void markFailWithReason(Throwable throwable) {
        this.status = EventProcessStatus.FAIL;
        this.failReason = throwable.getMessage();
    }

    public void incrementRetryCount() {
        retryCount++;
    }

}
