package com.example.mohago_nocar.course.domain.model.travelSpot;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelSpotFestival extends TravelSpot{

    private Long festivalId;

    @Builder
    private TravelSpotFestival(Long courseId, Integer visitOrder, Long festivalId) {
        super(courseId, visitOrder);
        this.festivalId = festivalId;
    }

    public TravelSpotFestival from(Long courseId, Integer visitOrder, Long festivalId) {
        return TravelSpotFestival.builder()
                .courseId(courseId)
                .visitOrder(visitOrder)
                .festivalId(festivalId)
                .build();
    }

}
