package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEventLog;
import com.example.mohago_nocar.course.domain.repository.TravelCourseOptimizedEventRepository;
import com.example.mohago_nocar.course.infrastructure.course.messaging.TravelCourseOptimizedEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TravelCourseEventService {

    private final TravelCourseOptimizedEventRepository travelCourseOptimizedEventRepository;
    private final TravelCourseOptimizedEventPublisher eventPublisher;

    @Transactional
    public TravelCourseOptimizedEventLog generate(TravelCourse course) {
        TravelCourseOptimizedEventLog courseOutbox = createCourseOutbox(course);
        return travelCourseOptimizedEventRepository.save(courseOutbox);
    }

    private TravelCourseOptimizedEventLog createCourseOutbox(TravelCourse course) {
        TravelCourseOptimizedEvent event = TravelCourseOptimizedEvent.of(course.getId(), course.getAnonymousUserId());
        return TravelCourseOptimizedEventLog.create(event);
    }

    public List<TravelCourseOptimizedEventLog> findUnpublished(int size) {
        return travelCourseOptimizedEventRepository.findByStatusInOrderByCreatedDateAsc(
                List.of(EventProcessStatus.PENDING), size);
    }

    public void publish(TravelCourseOptimizedEventLog travelCourseOptimizedEventLog) {
        eventPublisher.publish(travelCourseOptimizedEventLog.getPayload());
    }

    @Transactional
    public void markAsPublished(TravelCourseOptimizedEventLog outbox) {
        outbox.markAsPublished();
        travelCourseOptimizedEventRepository.save(outbox);
    }

    @Transactional
    public void processFailure(TravelCourseOptimizedEventLog outbox, Throwable throwable) {
        if (outbox.isFinalRetry()) {
            outbox.markFailWithReason(EventProcessStatus.FAIL, throwable);
            log.error("최대 재시도 횟수에 도달했습니다. outbox id: {}", outbox.getId());
        } else {
            outbox.incrementRetryCount();
        }
        travelCourseOptimizedEventRepository.save(outbox);
    }

}
