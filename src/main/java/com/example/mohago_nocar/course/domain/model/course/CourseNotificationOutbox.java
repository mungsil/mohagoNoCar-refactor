package com.example.mohago_nocar.course.domain.model.course;

import com.example.mohago_nocar.global.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseNotificationOutbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private Long travelCourseId;

    private String failReason;

    @Column(nullable = false)
    private Integer tryCount;

    @Column(nullable = false)
    private Boolean isSuccess;

    public static CourseNotificationOutbox create(Long courseId) {
        return CourseNotificationOutbox.builder()
                .travelCourseId(courseId)
                .tryCount(0)
                .isSuccess(false)
                .build();
    }

    @Builder
    private CourseNotificationOutbox(Long travelCourseId, String failReason, Integer tryCount, Boolean isSuccess) {
        this.travelCourseId = travelCourseId;
        this.failReason = failReason;
        this.tryCount = tryCount;
        this.isSuccess = isSuccess;
    }

    public void incrementTryCount(int count) {
        this.tryCount += count;
    }

    public void markSuccess() {
        this.isSuccess = true;
    }
}
