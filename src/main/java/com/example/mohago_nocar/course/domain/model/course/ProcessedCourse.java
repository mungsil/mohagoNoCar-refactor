package com.example.mohago_nocar.course.domain.model.course;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessedCourse implements Persistable<Long> {

    @Id
    @Column(unique = true)
    private Long travelCourseId;

    @Column(nullable = false)
    private Boolean result;

    public static ProcessedCourse of(long travelCourseId, boolean result) {
        return new ProcessedCourse(travelCourseId, result);
    }

    @Builder
    private ProcessedCourse(Long travelCourseId, Boolean result) {
        this.travelCourseId = travelCourseId;
        this.result = result;
    }

    @Override
    public Long getId() {
        return travelCourseId;
    }

    @Override
    public boolean isNew() {
        return true;
    }

}
