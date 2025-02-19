package com.example.mohago_nocar.plan.domain.model;

import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.place.domain.model.Place;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Location {

    private String name;
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

}
