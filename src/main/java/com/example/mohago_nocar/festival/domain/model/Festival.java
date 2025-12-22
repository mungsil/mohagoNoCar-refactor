package com.example.mohago_nocar.festival.domain.model;

import com.example.mohago_nocar.festival.domain.model.vo.ActivePeriod;
import com.example.mohago_nocar.global.common.domain.BaseEntity;
import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "festival")
@NoArgsConstructor(access = PROTECTED)
public class Festival extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Embedded
    private ActivePeriod activePeriod;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    private String address;

    @NotNull
    @Embedded
    private Coordinate coordinate;

    public static Festival from(String name, ActivePeriod activePeriod, String description, String address, Coordinate coordinate) {
        return Festival.builder()
                .name(name)
                .activePeriod(activePeriod)
                .description(description)
                .address(address)
                .coordinate(coordinate)
                .build();
    }

    @Builder
    private Festival(String name, ActivePeriod activePeriod, String description, String address, Coordinate coordinate) {
        this.name = name;
        this.activePeriod = activePeriod;
        this.description = description;
        this.address = address;
        this.coordinate = coordinate;
    }


    public boolean isDateDuringFestival(LocalDate travelDate) {
        return activePeriod.containsDate(travelDate);
    }

    public boolean isOpen(LocalDate travelDate) {
        return activePeriod.containsDate(travelDate);
    }

}
