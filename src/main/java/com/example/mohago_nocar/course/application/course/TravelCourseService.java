package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.application.dto.RouteStepDto;
import com.example.mohago_nocar.course.application.route.RouteFinder;
import com.example.mohago_nocar.course.application.route.RouteStepService;
import com.example.mohago_nocar.course.application.spot.TravelSpotService;
import com.example.mohago_nocar.course.domain.event.ThrottlingCompletedEvent;
import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEventConsumeExecution;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseCompletionMessage;
import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpot;
import com.example.mohago_nocar.course.domain.repository.CourseOptimizedEventConsumeExecutionRepository;
import com.example.mohago_nocar.course.domain.repository.CourseOptimizedEventRepository;
import com.example.mohago_nocar.course.domain.repository.TravelCourseRepository;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.course.presentation.dto.CreateTravelCourseRequestDto;
import com.example.mohago_nocar.course.presentation.dto.CreateOptimizedTravelCourseAcceptedResponseDto;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.global.common.exception.CustomException;
import com.example.mohago_nocar.global.common.exception.GlobalStatus;
import com.example.mohago_nocar.global.notification.application.user.UserNotificationDto;
import com.example.mohago_nocar.global.notification.application.user.UserNotificationOutboxService;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import com.example.mohago_nocar.user.domain.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TravelCourseService implements TravelCourseUseCase {

    private final UserUseCase userUseCase;
    private final TravelCourseRepository travelCourseRepository;
    private final TravelSpotService travelSpotService;
    private final RouteStepService routeStepService;
    private final RouteFinder routeFinder;
    private final CourseOptimizedEventRepository optimizedEventRepository;
    private final CourseOptimizedEventConsumeExecutionRepository optimizedEventConsumeExecutionRepository;
    private final UserNotificationOutboxService notificationOutboxService;

    @Override
    @Transactional
    public CreateOptimizedTravelCourseAcceptedResponseDto createOptimizedTravelCourse(CreateTravelCourseRequestDto request) {
        AnonymousUser user = userUseCase.getOrCreate(request.fcmToken());
        TravelCourse course = TravelCourse.create(user);
        travelCourseRepository.save(course);

        generateSpotsWithOptimizedOrder(request, course);

        saveOptimizedEvent(user, course);
        return CreateOptimizedTravelCourseAcceptedResponseDto.of(course.getId(), user.getId());
    }

    private void saveOptimizedEvent(AnonymousUser user, TravelCourse course) {
        CourseOptimizedEvent event = CourseOptimizedEvent.create(user, course);
        optimizedEventRepository.save(event);
    }

    private void generateSpotsWithOptimizedOrder(CreateTravelCourseRequestDto request, TravelCourse course) {
        Set<TravelSpot> spotsWithoutVisitOrder =
                travelSpotService.makeSpotsWithoutOrder(course, request.festivalId(), request.travelStartDate(), request.placeIds());

        Set<TravelSpot> spotsWithOrder =
                travelSpotService.determineOptimizedTravelOrder(spotsWithoutVisitOrder);

        travelSpotService.saveAll(spotsWithOrder);
    }

    @Override
    public CompletableFuture<List<RouteStep>> fetchTravelRoutesFromExternalApi(Long travelCourseId) {
        List<TravelSpot> travelSpots = travelSpotService.getByCourseId(travelCourseId);

        validateMinSize(travelSpots, 2);
        sortByVisitOrder(travelSpots);

        List<CompletableFuture<RouteStep>> routeFutures = routeFinder.findRouteWithThrottling(travelSpots);

        return CompletableFuture.allOf(routeFutures.toArray(new CompletableFuture[0]))
                .orTimeout(8, TimeUnit.SECONDS)
                .thenApply(completed -> routeFutures.stream().map(CompletableFuture::join).toList());
    }

    private void sortByVisitOrder(List<TravelSpot> travelSpots) {
        Collections.sort(travelSpots);
    }

    private void validateMinSize(List<? extends TravelSpot> travelSpotsInOrder, int minSize) {
        if (travelSpotsInOrder == null || travelSpotsInOrder.size() < minSize) {
            System.out.println("travelSpotInOrder: " + travelSpotsInOrder);
            throw new IllegalArgumentException("최소 2개 이상의 위치가 필요합니다.");
        }
    }

    @Override
    public List<? extends RouteStepDto> getOptimizedTravelCourseRoutes(Long courseId, UUID ownerUserId) {
        Objects.requireNonNull(courseId);
        Objects.requireNonNull(ownerUserId);

        TravelCourse course = travelCourseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(GlobalStatus.ENTITY_NOT_FOUND));

        if (!course.getAnonymousUserId().equals(ownerUserId)) {
            throw new CustomException(GlobalStatus.FORBIDDEN);
        }

        // todo ProcessCourse 조회
//        if (course.getCourseStatus() != TravelCourseStatus.SUCCEEDED) {
//            throw new CustomException(CourseErrorCode.TRAVEL_COURSE_OPTIMIZATION_INCOMPLETE);
//        }

        List<TravelSpot> travelSpots = travelSpotService.getByCourseId(course.getId());
        Collections.sort(travelSpots);

        List<RouteStepDto> routesBetweenSpots = new ArrayList<>();
        for (int i = 0; i < travelSpots.size() - 1; i++) {
            TravelSpot origin = travelSpots.get(i);
            TravelSpot destination = travelSpots.get(i + 1);
            RouteStep route = routeStepService.getByOriginAndDestination(origin, destination);
            RouteStepDto dto = TravelCourseConverter.convertToRouteStepDto(origin, destination, route);
            routesBetweenSpots.add(dto);
        }

        return routesBetweenSpots;
    }

    @Override
    public Optional<TravelCourse> findById(Long travelCourseId) {
        return travelCourseRepository.findById(travelCourseId);
    }

    @Override
    public List<CourseOptimizedEvent> getOptimizedCourseEvents(int size, EventProcessStatus... eventProcessStatus) {
        return optimizedEventRepository.findTop10ByStatusInOrderByCreatedDateAsc(size, List.of(eventProcessStatus));
    }

    @Transactional
    @Override
    public void completeOptimizedEventExecution(CourseOptimizedEvent event, EventProcessStatus status, String detail) {
        event.updateProcessStatus(status);
        optimizedEventRepository.save(event);

        CourseOptimizedEventConsumeExecution execution;
        switch (status){
            case SUCCESS -> execution = CourseOptimizedEventConsumeExecution.success(event);
            case RETRYABLE_FAIL, FATAL_FAIL -> execution = CourseOptimizedEventConsumeExecution.failWithDetail(event, detail);
            default -> throw new IllegalArgumentException("Unexpected value: " + status);
        }

        if (status == EventProcessStatus.SUCCESS ||
                status == EventProcessStatus.RETRYABLE_FAIL) {
            UserNotificationDto notificationDto = createNotificationMessage(event, false);
            notificationOutboxService.save(notificationDto);
        }

        optimizedEventConsumeExecutionRepository.save(execution);
    }

    private UserNotificationDto createNotificationMessage(CourseOptimizedEvent event, boolean isSuccess) {
        TravelCourseCompletionMessage message = isSuccess ?
                TravelCourseCompletionMessage.SUCCESS : TravelCourseCompletionMessage.FAILURE;

        return new UserNotificationDto(
                message.getTitle(),
                message.getBody(),
                event.getAnonymousUserId(),
                Map.of("travelCourseId", String.valueOf(event.getTravelCourseId()))
        );
    }

}
