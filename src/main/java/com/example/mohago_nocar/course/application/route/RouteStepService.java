package com.example.mohago_nocar.course.application.route;

import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpot;
import com.example.mohago_nocar.course.domain.repository.RouteStepRepository;
import com.example.mohago_nocar.global.common.exception.CustomException;
import com.example.mohago_nocar.global.common.exception.GlobalStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteStepService {

    private final RouteStepRepository routeStepRepository;
    private final RouteFinder routeStepFinder;

    @Transactional
    public List<RouteStep> saveAll(List<RouteStep> routeSteps) {
            return routeStepRepository.saveAll(routeSteps);
    }

    // todo 예외 처리, 메서드 이름이 올바른가 점검
    private CompletableFuture<Void> saveWhenAllCompleteWithTimeout(List<CompletableFuture<RouteStep>> routeStepFutures, int timeoutInSec) {
        return waitAllCompleteWithTimeout(routeStepFutures, timeoutInSec)
                .thenRun(() -> {
                    List<RouteStep> routeSteps = routeStepFutures.stream().map(CompletableFuture::join).toList();
                    routeStepRepository.saveAll(routeSteps);
                });
    }

    private CompletableFuture<Void> waitAllCompleteWithTimeout(List<CompletableFuture<RouteStep>> routeStepFutures, int timeoutInSec) {
        return CompletableFuture.allOf(routeStepFutures.toArray(new CompletableFuture[routeStepFutures.size()]))
                .orTimeout(timeoutInSec, TimeUnit.SECONDS);
    }

    public RouteStep getByOriginAndDestination(TravelSpot origin, TravelSpot destination) {
        return routeStepRepository.findByOriginAndDestination(origin.getId(), destination.getId())
                .orElseThrow(() -> {
                    log.error("주어진 출발지와 목적지 사이에 경로가 존재하지 않습니다. origin={}, destination={}", origin, destination);
                    return new CustomException(GlobalStatus.ENTITY_NOT_FOUND);
                });
    }

    public List<RouteStep> findAll(List<Long> ids) {
        return routeStepRepository.findByIds(ids);
    }
}
