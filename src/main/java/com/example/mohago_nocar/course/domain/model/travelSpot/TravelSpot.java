package com.example.mohago_nocar.course.domain.model.travelSpot;

import com.example.mohago_nocar.global.common.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TravelSpot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    private Long courseId;

    @NotNull
    private Integer visitOrder;

    protected TravelSpot(Long courseId, Integer visitOrder) {
        this.courseId = courseId;
        this.visitOrder = visitOrder;
    }

    /*    public static TravelSpot from(Long courseId, String placeId, Integer visitOrder) {
        return TravelSpot.builder()
                .courseId(courseId)
                .placeId(placeId)
                .visitOrder(visitOrder)
                .build();
    }

    @Builder
    private TravelSpot(Long courseId, String placeId, Integer visitOrder) {
        this.courseId = courseId;
        this.placeId = placeId;
        this.visitOrder = visitOrder;
    }*/

}