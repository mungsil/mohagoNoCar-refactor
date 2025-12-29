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
public class TravelCourseOptimizedEventHandleHistory implements Persistable<Long> {

    @Id
    @Column(unique = true)
    private Long travelCourseId;

    public static TravelCourseOptimizedEventHandleHistory of(Long travelCourseId) {
        return new TravelCourseOptimizedEventHandleHistory(travelCourseId);
    }

    @Builder
    private TravelCourseOptimizedEventHandleHistory(Long travelCourseId) {
        this.travelCourseId = travelCourseId;
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
