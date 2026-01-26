package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.application.route.RouteStepService;
import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.transit.infrastructure.error.exception.ODsayRouteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class TravelCourseOptimizedEventDispatcher {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final TravelCourseUseCase travelCourseUseCase;
    private final RouteStepService routeStepService;
    private final TransactionTemplate transactionTemplate;

    public void dispatch() {
        executorService.submit(() -> {
            while (true) {
                List<CourseOptimizedEvent> unProcessed = travelCourseUseCase.getOptimizedCourseEvents(
                        10, EventProcessStatus.CREATED, EventProcessStatus.PENDING_RETRY);
                if (unProcessed.isEmpty()) {
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(3));
                }

                for (CourseOptimizedEvent event : unProcessed) {
                    try {
                        processEvent(event);
                    } catch (Exception ex) { // tx 내부에서 발생한 에러 전달 확인
                        handleException(ex, event);
                    }
                }
            }
        });
    }

    public void processEvent(CourseOptimizedEvent event) {
        CompletableFuture<List<RouteStep>> future = travelCourseUseCase.fetchTravelRoutesFromExternalApi(event.getTravelCourseId());
        future.thenAccept(routeSteps -> {
                    transactionTemplate.executeWithoutResult(tx -> {
                        routeStepService.saveAll(routeSteps);
                        travelCourseUseCase.completeOptimizedEventExecution(event, EventProcessStatus.SUCCESS, null);
                    });
                })
                .exceptionally(throwable -> {
                    handleException((Exception) throwable, event);
                    return null;
                });
    }

    private void handleException(Exception ex, CourseOptimizedEvent event) {
        log.error("Travel course optimized event processing failed", ex);
        EventProcessStatus status;

        if (isRetryable(ex)) {
            status = EventProcessStatus.RETRYABLE_FAIL;
        } else {
            status = EventProcessStatus.FATAL_FAIL;
        }

        travelCourseUseCase.completeOptimizedEventExecution(event, status, ex.toString());
    }

    private boolean isRetryable(Exception exception) {
        if (exception instanceof SocketTimeoutException ||
                exception instanceof ConnectException ||
                exception instanceof HttpTimeoutException) {
            return true;
        }

        if (exception instanceof ODsayRouteException oDsayRouteException) {
            return oDsayRouteException.getErrorCode().isTooManyRequests() ||
                    oDsayRouteException.getErrorCode().isServerError();
        }

        return false;
    }

}
