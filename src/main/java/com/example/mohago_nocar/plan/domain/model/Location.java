package com.example.mohago_nocar.plan.domain.model;

import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.place.domain.model.Place;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 장소의 이름과 경위도
 */
@Getter
@NoArgsConstructor
@ToString
@Embeddable
public class Location {

    private String name;

    @Embedded
    private Coordinate coordinate;

    public static Location of(Festival festival) {
        return Location.builder()
                .name(festival.getName())
                .coordinate(festival.getCoordinate())
                .build();
    }

    public static Location of(Place place) {
        return Location.builder()
                .name(place.getName())
                .coordinate(place.getCoordinate())
                .build();
    }

    public static Location of(String name, Coordinate coordinate) {
        return Location.builder()
                .name(name)
                .coordinate(coordinate)
                .build();
    }

    @Builder
    private Location(String name, Coordinate coordinate) {
        this.name = name;
        this.coordinate = coordinate;
    }

    public boolean hasSameCoordinate(Location destination) {
        return this.coordinate.equals(destination.coordinate);
    }

}
