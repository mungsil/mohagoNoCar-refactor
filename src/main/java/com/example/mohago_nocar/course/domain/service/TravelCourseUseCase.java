package com.example.mohago_nocar.course.domain.service;

import com.example.mohago_nocar.course.application.dto.RouteStepDto;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseStatus;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.presentation.dto.CreateTravelCourseRequestDto;
import com.example.mohago_nocar.course.presentation.dto.CreateOptimizedTravelCourseAcceptedResponseDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TravelCourseUseCase {

    /**
     * 여행 코스 내 장소 간 대중교통 이동 경로를 생성합니다.
     * @param travelCourseId 장소 방문 순서가 결정된 여행 코스의 아이디
     */
    void generateTransitRoute(Long travelCourseId);

    /**
     * 여행 장소들을 방문할 순서를 결정합니다.
     * 모든 장소를 가장 빠르게 거칠 수 있는 최적화된 방문 순서를 제공합니다.
     */
    CreateOptimizedTravelCourseAcceptedResponseDto createOptimizedTravelCourse(CreateTravelCourseRequestDto request);

    List<? extends RouteStepDto> getOptimizedTravelCourseRoutes(Long courseId, UUID ownerUserId);

    Optional<TravelCourse> findById(Long travelCourseId);

    void updateUncompletedCourseStatus(Long travelCourseId, TravelCourseStatus courseStatus);

}
