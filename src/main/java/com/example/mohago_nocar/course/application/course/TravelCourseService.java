package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.application.dto.RouteStepDto;
import com.example.mohago_nocar.course.application.route.RouteFinder;
import com.example.mohago_nocar.course.application.route.RouteStepService;
import com.example.mohago_nocar.course.application.spot.TravelSpotService;
import com.example.mohago_nocar.course.domain.model.course.*;
import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import com.example.mohago_nocar.course.domain.model.travelSpot.TravelSpot;
import com.example.mohago_nocar.course.domain.repository.CourseOptimizedEventConsumeRepository;
import com.example.mohago_nocar.course.domain.repository.CourseOptimizedEventRepository;
import com.example.mohago_nocar.course.domain.repository.TravelCourseRepository;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.course.infrastructure.course.CourseNotificationOutboxRepository;
import com.example.mohago_nocar.course.presentation.dto.CreateTravelCourseRequestDto;
import com.example.mohago_nocar.course.presentation.dto.CreateOptimizedTravelCourseAcceptedResponseDto;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.global.common.exception.CustomException;
import com.example.mohago_nocar.global.common.exception.GlobalStatus;
import com.example.mohago_nocar.global.util.StackTraceExtractor;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import com.example.mohago_nocar.user.domain.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final TravelCourseRepository courseRepository;
    private final TravelSpotService travelSpotService;
    private final RouteStepService routeStepService;
    private final RouteFinder routeFinder;
    private final CourseOptimizedEventRepository optimizedEventRepository;
    private final CourseOptimizedEventConsumeRepository optimizedEventConsumeRepository;
    private final StackTraceExtractor stackTraceExtractor;
    private final CourseNotificationOutboxRepository notificationOutboxRepository;

    @Override
    @Transactional
    public CreateOptimizedTravelCourseAcceptedResponseDto createOptimizedTravelCourse(CreateTravelCourseRequestDto request) {
        AnonymousUser user = userUseCase.getOrCreate(request.fcmToken());
        TravelCourse course = TravelCourse.create(user);
        courseRepository.save(course);

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

        TravelCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CustomException(GlobalStatus.ENTITY_NOT_FOUND));

        if (!course.getAnonymousUserId().equals(ownerUserId)) {
            throw new CustomException(GlobalStatus.FORBIDDEN);
        }

        // todo course event == success 여야 함.
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
        return courseRepository.findById(travelCourseId);
    }

    @Override
    public List<CourseOptimizedEvent> getOldestOptimizedCourseEvents(int size, List<EventProcessStatus> eventProcessStatuses) {
        return optimizedEventRepository.findTopNByStatusInOrderByCreatedDateAsc(size, eventProcessStatuses);
    }

    @Transactional
    @Override
    public void completeOptimizedEventConsumeWithFailure(CourseOptimizedEvent event, Exception exception) {
        CourseOptimizedEventConsume consumed = event.consumeFailure(exception, stackTraceExtractor);
        optimizedEventRepository.save(event);
        optimizedEventConsumeRepository.save(consumed);
        notificationOutboxRepository.save(CourseNotificationOutbox.create(event.getTravelCourseId()));
    }

    @Transactional
    @Override
    public void completeOptimizedEventConsumeWithSuccess(CourseOptimizedEvent event) {
        CourseOptimizedEventConsume consumed = event.consumeSuccess();
        optimizedEventRepository.save(event);
        optimizedEventConsumeRepository.save(consumed);
        notificationOutboxRepository.save(CourseNotificationOutbox.create(event.getTravelCourseId()));
    }

    @Override
    public AnonymousUser getRequestUserOrThrow(Long travelCourseId) {
        return courseRepository.findUserByCourseId(travelCourseId)
                .orElseThrow(() -> new CustomException(GlobalStatus.ENTITY_NOT_FOUND));
    }

    @Override
    public CourseOptimizedEvent getOptimizedEventOrThrow(Long travelCourseId) {
        return courseRepository.findOptimizedEventByCourseId(travelCourseId)
                .orElseThrow(() -> new CustomException(GlobalStatus.ENTITY_NOT_FOUND));
    }

}
