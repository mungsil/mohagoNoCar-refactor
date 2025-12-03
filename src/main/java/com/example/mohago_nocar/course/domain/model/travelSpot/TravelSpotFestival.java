package com.example.mohago_nocar.course.domain.model.travelSpot;

import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.plan.domain.model.Location;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@DiscriminatorValue("festival")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelSpotFestival extends TravelSpot{

    private Long festivalId;

    @Builder
    private TravelSpotFestival(Long courseId, Integer visitOrder, Long festivalId, Location location){
        super(courseId, visitOrder, location);
        this.festivalId = festivalId;
    }

    public TravelSpotFestival create(Long courseId, Integer visitOrder, Long festivalId) {
        return TravelSpotFestival.builder()
                .courseId(courseId)
                .visitOrder(visitOrder)
                .festivalId(festivalId)
                .build();
    }

    public static TravelSpotFestival createUnOrderedSpot(
            TravelCourse course, Festival festival) {
        return TravelSpotFestival.builder()
                .courseId(course.getId())
                .visitOrder(null)
                .festivalId(festival.getId())
                .location(Location.of(festival))
                .build();
    }

}
