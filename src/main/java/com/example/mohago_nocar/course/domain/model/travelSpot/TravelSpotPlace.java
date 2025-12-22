package com.example.mohago_nocar.course.domain.model.travelSpot;

import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.place.domain.model.Place;
import com.example.mohago_nocar.plan.domain.model.Location;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@DiscriminatorValue("place")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelSpotPlace extends TravelSpot{

    private String placeId;

    @Builder
    private TravelSpotPlace(Long courseId, Integer visitOrder, String placeId, Location location) {
        super(courseId, visitOrder, location);
        this.placeId = placeId;
    }

    public TravelSpotPlace create(Long courseId, Integer visitOrder, String placeId) {
        return TravelSpotPlace.builder()
                .courseId(courseId)
                .visitOrder(visitOrder)
                .placeId(placeId)
                .build();
    }

    public static TravelSpotPlace createWithNoOrder(TravelCourse course, Place place) {
        return TravelSpotPlace.builder()
                .courseId(course.getId())
                .visitOrder(null)
                .placeId(place.getKakaoId())
                .location(Location.of(place))
                .build();
    }

}
