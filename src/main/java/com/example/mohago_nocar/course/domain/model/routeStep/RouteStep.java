package com.example.mohago_nocar.course.domain.model.routeStep;

import com.example.mohago_nocar.global.common.domain.BaseEntity;
import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.global.util.DurationToIntervalConverter;
import com.example.mohago_nocar.plan.domain.model.Location;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class RouteStep extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    private Long courseId;

    @NotNull
    private Integer distance;

    @NotNull
    private Integer stepOrder;

    // todo subpath vs jsonB
    // JSONB
    private String detailPaths;

    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "start_name")),
            @AttributeOverride(name = "coordinate.latitude", column = @Column(name = "start_latitude")),
            @AttributeOverride(name = "coordinate.longitude", column = @Column(name = "start_longitude"))
    })
    private Location origin;

    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "end_name")),
            @AttributeOverride(name = "coordinate.latitude", column = @Column(name = "end_latitude")),
            @AttributeOverride(name = "coordinate.longitude", column = @Column(name = "end_longitude"))
    })
    private Location destination;

    @NotNull
    @Convert(converter = DurationToIntervalConverter.class)
    private Duration timeTaken;

    public static RouteStep from(Long courseId, Integer distance, Integer stepOrder,
                                 Location origin, Location destination, Duration timeTaken
    ) {
        return RouteStep.builder()
                .courseId(courseId)
                .distance(distance)
                .stepOrder(stepOrder)
                .origin(origin)
                .destination(destination)
                .timeTaken(timeTaken)
                .build();
    }

    @Builder
    private RouteStep(Long courseId, Integer distance, Integer stepOrder,
                      Location origin, Location destination, Duration timeTaken
    ) {
        this.courseId = courseId;
        this.distance = distance;
        this.stepOrder = stepOrder;
        this.origin = origin;
        this.destination = destination;
        this.timeTaken = timeTaken;
    }

}
