package com.example.mohago_nocar.place.domain.model;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;

@Getter
@NoArgsConstructor
public class Place {

    @NotNull
    private String id;

    @NotNull
    private String name;

    @NotNull
    private Coordinate coordinate;

    @NotNull
    private String address;

    @NotNull
    private String placeUrl;

    @NotNull
    @Enumerated(value = STRING)
    private PlaceCategory category;

    public static Place from(
            String id,
            String name,
            Coordinate coordinate,
            String address,
            String placeUrl,
            PlaceCategory category
    ) {
        return Place.builder()
                .id(id)
                .name(name)
                .coordinate(coordinate)
                .address(address)
                .placeUrl(placeUrl)
                .category(category)
                .build();
    }

    @Builder
    private Place(
            String id,
            String name,
            Coordinate coordinate,
            String address,
            String placeUrl,
            PlaceCategory category
    ) {
        this.id = id;
        this.name = name;
        this.coordinate = coordinate;
        this.address = address;
        this.placeUrl = placeUrl;
        this.category = category;
    }

}
