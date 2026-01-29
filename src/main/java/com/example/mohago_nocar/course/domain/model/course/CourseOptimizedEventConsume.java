package com.example.mohago_nocar.course.domain.model.course;

import com.example.mohago_nocar.global.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseOptimizedEventConsume extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private Long courseOptimizedEventId;

    @Column(nullable = false)
    private Boolean isSuccess;

    @Column(nullable = false)
    private String description;

    public static CourseOptimizedEventConsume success(CourseOptimizedEvent event) {
        return CourseOptimizedEventConsume.builder()
                .courseOptimizedEventId(event.getId())
                .isSuccess(true)
                .build();
    }

    public static CourseOptimizedEventConsume failWithDetail(CourseOptimizedEvent event, String detail) {
        return CourseOptimizedEventConsume.builder()
                .courseOptimizedEventId(event.getId())
                .isSuccess(false)
                .description(detail)
                .build();
    }

    @Builder
    private CourseOptimizedEventConsume(Long courseOptimizedEventId, Boolean isSuccess, String description) {
        this.courseOptimizedEventId = courseOptimizedEventId;
        this.isSuccess = isSuccess;
        this.description = description;
    }
}
