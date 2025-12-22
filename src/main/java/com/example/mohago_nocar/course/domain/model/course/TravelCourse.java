package com.example.mohago_nocar.course.domain.model.course;

import com.example.mohago_nocar.global.common.domain.BaseEntity;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "travel_course")
public class TravelCourse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID anonymousUserId;

    @NotNull
    @Column(nullable = false)
    private Boolean notificationSent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus courseStatus;

    public static TravelCourse create(AnonymousUser user, CourseStatus courseStatus) {
        return TravelCourse.builder()
                .anonymousUserId(user.getId())
                .courseStatus(courseStatus)
                .notificationSent(false)
                .build();
    }

    @Builder
    private TravelCourse(UUID anonymousUserId, CourseStatus courseStatus, Boolean notificationSent) {
        this.anonymousUserId = anonymousUserId;
        this.courseStatus = courseStatus;
        this.notificationSent = notificationSent;
    }

    public void markNotificationSent() {
        this.notificationSent = true;
    }

    public void updateStatus(CourseStatus courseStatus) {
        this.courseStatus  = courseStatus;
    }

}
