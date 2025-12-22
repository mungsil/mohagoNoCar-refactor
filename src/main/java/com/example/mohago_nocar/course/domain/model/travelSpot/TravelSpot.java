package com.example.mohago_nocar.course.domain.model.travelSpot;

import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.global.common.domain.BaseEntity;
import com.example.mohago_nocar.place.domain.model.Place;
import com.example.mohago_nocar.plan.domain.model.Location;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "spot_type")
@Table(name = "travel_spot")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TravelSpot extends BaseEntity implements Comparable<TravelSpot> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    private Long courseId;

    private Integer visitOrder;

    @NotNull
    @Embedded
    private Location location; // snapshot

    protected TravelSpot(Long courseId, Integer visitOrder, Location location) {
        this.courseId = courseId;
        this.visitOrder = visitOrder;
        this.location = location;
    }

    @Override
    public int compareTo(TravelSpot other) {
        Objects.requireNonNull(this.getVisitOrder(), "방문 순서가 정해지지 않은 장소입니다.");
        Objects.requireNonNull(other.getVisitOrder(), "방문 순서가 정해지지 않은 장소입니다.");

        return Comparator.comparing(TravelSpot::getVisitOrder)
                .compare(this, other);
    }

    public void setOrder(int i) {
        visitOrder = i;
    }

}