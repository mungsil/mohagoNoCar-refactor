package com.example.mohago_nocar.course.domain.repository;

import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RouteStepRepository {
    List<RouteStep> saveAll(List<RouteStep> routeSteps);

    Optional<RouteStep> findByOriginAndDestination(Long originSpotId, Long destinationSpotId);

    List<RouteStep> findByIds(List<Long> ids);
}
