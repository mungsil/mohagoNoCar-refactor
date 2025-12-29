package com.example.mohago_nocar.course.domain.model.routeStep;

import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpot;
import com.example.mohago_nocar.global.common.domain.BaseEntity;
import com.example.mohago_nocar.transit.domain.model.SubPath;
import com.example.mohago_nocar.transit.domain.model.TransitRoute;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "route_step",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"originSpotId", "destinationSpotId"})
)
@ToString
@NoArgsConstructor(access = PROTECTED)
public class RouteStep extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long originSpotId;

    private Long destinationSpotId;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<SubPath> detailPaths;

    @NotNull
    private Double distanceKm;

    private Integer timeTakenMin;

    public static RouteStep from(
            TravelSpot origin, TravelSpot destination, TransitRoute transitRoute
    ) {
        List<SubPath> subPaths = transitRoute.getSubPaths();
        return RouteStep.builder()
                .originSpotId(origin.getId())
                .destinationSpotId(destination.getId())
                .timeTakenMin(transitRoute.getTotalTime())
                .distanceKm(transitRoute.getTotalDistance())
                .detailPaths(subPaths)
                .build();
    }

    @Builder
    private RouteStep(Long originSpotId, Long destinationSpotId, List<SubPath> detailPaths,
                      Double distanceKm, Integer timeTakenMin) {
        this.originSpotId = originSpotId;
        this.destinationSpotId = destinationSpotId;
        this.detailPaths = detailPaths;
        this.distanceKm = distanceKm;
        this.timeTakenMin = timeTakenMin;
    }

}