package com.example.mohago_nocar.course.domain.model.course;

import com.example.mohago_nocar.global.common.domain.BaseEntity;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseOptimizedEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // todo 유니크 제약 조건 추가
    @Column(unique = true)
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

    public void updateProcessStatus(EventProcessStatus status) {
        this.status = status;
    }
}
