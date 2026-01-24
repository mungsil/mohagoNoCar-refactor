package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.repository.TravelCourseEventRepository;
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

    private final TravelCourseEventRepository travelCourseEventRepository;
    private final TravelCourseOptimizedEventPublisher eventPublisher;

    @Transactional
    public TravelCourseOptimizedEvent generate(TravelCourse course) {
        TravelCourseOptimizedEvent courseOutbox = createCourseOutbox(course);
        return travelCourseEventRepository.save(courseOutbox);
    }

    private TravelCourseOptimizedEvent createCourseOutbox(TravelCourse course) {
        com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent event = com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent.of(course.getId(), course.getAnonymousUserId());
        return TravelCourseOptimizedEvent.create(event);
    }

    public List<TravelCourseOptimizedEvent> findUnpublished(int size) {
        return travelCourseEventRepository.findByStatusInOrderByCreatedDateAsc(
                List.of(EventProcessStatus.PENDING), size);
    }

    public void publish(TravelCourseOptimizedEvent travelCourseOptimizedEvent) {
        eventPublisher.publish(travelCourseOptimizedEvent.getPayload());
    }

    @Transactional
    public void markAsPublished(TravelCourseOptimizedEvent outbox) {
        outbox.markAsPublished();
        travelCourseEventRepository.save(outbox);
    }

    @Transactional
    public void processFailure(TravelCourseOptimizedEvent outbox, Throwable throwable) {
        if (outbox.isFinalRetry()) {
            outbox.markFailWithReason(EventProcessStatus.FAIL, throwable);
            log.error("최대 재시도 횟수에 도달했습니다. outbox id: {}", outbox.getId());
        } else {
            outbox.incrementRetryCount();
        }
        travelCourseEventRepository.save(outbox);
    }

}
