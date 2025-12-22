package com.example.mohago_nocar.course.infrastructure.route;

import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouteStepJpaRepository extends JpaRepository<RouteStep, Long> {

    Optional<RouteStep> findByOriginSpotIdAndDestinationSpotId(Long originSpotId, Long destinationSpotId);

}
