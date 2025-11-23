package com.example.mohago_nocar.course.domain.model.travelSpot;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelSpotPlace extends TravelSpot{

    private String placeId;

    @Builder
    private TravelSpotPlace(Long courseId, Integer visitOrder, String placeId) {
        super(courseId, visitOrder);
        this.placeId = placeId;
    }

    public TravelSpotPlace from(Long courseId, Integer visitOrder, String placeId) {
        return TravelSpotPlace.builder()
                .courseId(courseId)
                .visitOrder(visitOrder)
                .placeId(placeId)
                .build();
    }

}
