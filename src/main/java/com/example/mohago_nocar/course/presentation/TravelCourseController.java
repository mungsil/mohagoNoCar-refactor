package com.example.mohago_nocar.course.presentation;

import com.example.mohago_nocar.course.application.dto.RouteStepDto;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.course.presentation.dto.CreateTravelCourseRequestDto;
import com.example.mohago_nocar.course.presentation.dto.CreateOptimizedTravelCourseAcceptedResponseDto;
import com.example.mohago_nocar.course.presentation.dto.GetOptimizedTravelCourseResponseDto;
import com.example.mohago_nocar.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/travel/courses")
public class TravelCourseController {

    private final TravelCourseUseCase travelCourseUseCase;

    @PostMapping("/")
    public ApiResponse<CreateOptimizedTravelCourseAcceptedResponseDto> createOptimizedTravelCourseWithRoute(
            @RequestBody @Valid CreateTravelCourseRequestDto request
    ) {
        CreateOptimizedTravelCourseAcceptedResponseDto response = travelCourseUseCase.createOptimizedTravelCourse(request);
        return ApiResponse.ok(response);
    }

    @GetMapping("/{courseId}")
    public ApiResponse<GetOptimizedTravelCourseResponseDto> getOptimizedTravelCourseWithRoutes(
            @PathVariable Long courseId,
            @RequestParam UUID ownerUserId
            ) {
        List<? extends RouteStepDto> course = travelCourseUseCase.getOptimizedTravelCourseRoutes(courseId, ownerUserId);
        return ApiResponse.ok(GetOptimizedTravelCourseResponseDto.of(course));
    }

}
