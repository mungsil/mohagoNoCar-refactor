package com.example.mohago_nocar.course.infrastructure.route;

import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import com.example.mohago_nocar.course.domain.repository.RouteStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RouteStepRepositoryImpl implements RouteStepRepository {

    private final RouteStepJpaRepository routeStepJpaRepository;

    @Override
    public List<RouteStep> saveAll(List<RouteStep> routeSteps) {
        return routeStepJpaRepository.saveAll(routeSteps);
    }

    @Override
    public Optional<RouteStep> findByOriginAndDestination(Long originSpotId, Long destinationSpotId) {
        return routeStepJpaRepository.findByOriginSpotIdAndDestinationSpotId(originSpotId, destinationSpotId);
    }

    @Override
    public List<RouteStep> findByIds(List<Long> ids) {
        return routeStepJpaRepository.findAllById(ids);
    }

}
