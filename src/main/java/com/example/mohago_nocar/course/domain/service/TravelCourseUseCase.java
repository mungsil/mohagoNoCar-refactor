package com.example.mohago_nocar.course.domain.service;

import com.example.mohago_nocar.course.application.dto.RouteStepDto;
import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import com.example.mohago_nocar.course.presentation.dto.CreateTravelCourseRequestDto;
import com.example.mohago_nocar.course.presentation.dto.CreateOptimizedTravelCourseAcceptedResponseDto;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TravelCourseUseCase {

    /**
     * 여행 코스 내 장소 간 대중교통 이동 경로를 구합니다.
     * @param travelCourseId 장소 방문 순서가 결정된 여행 코스의 아이디
     */
    CompletableFuture<List<RouteStep>> fetchTravelRoutesFromExternalApi(Long travelCourseId);

    /**
     * 주어진 장소들을 가장 빠르게 방문할 수 있는 최적화된 순서를 찾습니다.
     */
    CreateOptimizedTravelCourseAcceptedResponseDto createOptimizedTravelCourse(CreateTravelCourseRequestDto request);

    List<? extends RouteStepDto> getOptimizedTravelCourseRoutes(Long courseId, UUID ownerUserId);

    Optional<TravelCourse> findById(Long travelCourseId);

    List<CourseOptimizedEvent> getOptimizedCourseEvents(int i, EventProcessStatus... eventProcessStatus);

    void completeOptimizedEventExecution(CourseOptimizedEvent event, EventProcessStatus eventProcessStatus, String detail);

}
