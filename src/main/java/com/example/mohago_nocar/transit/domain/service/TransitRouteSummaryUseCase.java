package com.example.mohago_nocar.transit.domain.service;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.transit.domain.model.RouteMetrics;
import org.springframework.util.RouteMatcher;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface TransitRouteSummaryUseCase {

    /**
     * 출발지와 목적지 리스트 내에 있는 각 목적지 사이의 이동 요약 정보를 구합니다.
     * @param origin 출발지의 경위도
     * @param destinations 목적지 경위도 집합
     * @return 이동 시간 및 거리
     */
    CompletableFuture<List<RouteMetrics>> getRouteSummary(Coordinate origin, Set<Coordinate> destinations);

}
