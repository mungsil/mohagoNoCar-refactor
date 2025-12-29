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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TravelCourseStatus courseStatus;

    public static TravelCourse create(AnonymousUser user, TravelCourseStatus courseStatus) {
        return TravelCourse.builder()
                .anonymousUserId(user.getId())
                .courseStatus(courseStatus)
                .build();
    }

    @Builder
    private TravelCourse(UUID anonymousUserId, TravelCourseStatus courseStatus) {
        this.anonymousUserId = anonymousUserId;
        this.courseStatus = courseStatus;
    }

    public void updateStatus(TravelCourseStatus courseStatus) {
        this.courseStatus  = courseStatus;
    }

}
