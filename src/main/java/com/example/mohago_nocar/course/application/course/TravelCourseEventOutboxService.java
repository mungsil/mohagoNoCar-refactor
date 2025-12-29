package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.domain.event.TravelCourseOptimizedEvent;
import com.example.mohago_nocar.global.common.domain.OutboxStatus;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEventOutbox;
import com.example.mohago_nocar.course.domain.repository.TravelCourseEventOutboxRepository;
import com.example.mohago_nocar.course.infrastructure.course.messaging.TravelCourseOptimizedEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TravelCourseEventOutboxService {

    private final TravelCourseEventOutboxRepository travelCourseEventOutboxRepository;
    private final TravelCourseOptimizedEventPublisher eventPublisher;

    @Transactional
    public TravelCourseOptimizedEventOutbox generate(TravelCourse course) {
        TravelCourseOptimizedEventOutbox courseOutbox = createCourseOutbox(course);
        return travelCourseEventOutboxRepository.save(courseOutbox);
    }

    private TravelCourseOptimizedEventOutbox createCourseOutbox(TravelCourse course) {
        TravelCourseOptimizedEvent event = TravelCourseOptimizedEvent.of(course.getId(), course.getAnonymousUserId());
        return TravelCourseOptimizedEventOutbox.create(event);
    }

    public List<TravelCourseOptimizedEventOutbox> findUnpublished(int size) {
        return travelCourseEventOutboxRepository.findByStatusInOrderByCreatedDateAsc(
                List.of(OutboxStatus.PENDING), size);
    }

    public void publish(TravelCourseOptimizedEventOutbox travelCourseOptimizedEventOutbox) {
        eventPublisher.publish(travelCourseOptimizedEventOutbox.getPayload());
    }

    @Transactional
    public void markAsPublished(TravelCourseOptimizedEventOutbox outbox) {
        outbox.markAsPublished();
        travelCourseEventOutboxRepository.save(outbox);
    }

    @Transactional
    public void processFailure(TravelCourseOptimizedEventOutbox outbox, Throwable throwable) {
        if (outbox.isFinalRetry()) {
            outbox.markFailWithReason(OutboxStatus.FAIL, throwable);
            log.error("최대 재시도 횟수에 도달했습니다. outbox id: {}", outbox.getId());
        } else {
            outbox.incrementRetryCount();
        }
        travelCourseEventOutboxRepository.save(outbox);
    }

}
