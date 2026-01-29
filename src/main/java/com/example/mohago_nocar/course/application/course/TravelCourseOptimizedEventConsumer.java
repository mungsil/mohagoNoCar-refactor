package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.application.route.RouteStepService;
import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class TravelCourseOptimizedEventConsumer {

    private final TravelCourseUseCase travelCourseUseCase;
    private final RouteStepService routeStepService;
    private final TransactionTemplate transactionTemplate;

    public void consume(CourseOptimizedEvent event) {
        log.info("Consuming event: {}", event);
        // 외부 api 호출 작업을 트랜잭션에서 배제하여 tx 점유 시간 단축
        CompletableFuture<List<RouteStep>> future = travelCourseUseCase.fetchTravelRoutesFromExternalApi(event.getTravelCourseId());

        future.thenAccept(routeSteps -> {
                    transactionTemplate.executeWithoutResult(tx -> {
                        routeStepService.saveAll(routeSteps);
                        travelCourseUseCase.completeOptimizedEventConsumeWithSuccess(event);
                    });
                })
                .exceptionally(throwable -> {
                    log.error("Travel course optimized event processing failed", throwable);
                    travelCourseUseCase.completeOptimizedEventConsumeWithFailure(event, (Exception) throwable);
                    return null;
                });
    }

}
