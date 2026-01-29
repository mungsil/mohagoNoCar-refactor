package com.example.mohago_nocar.course.infrastructure.course;

import com.example.mohago_nocar.course.domain.model.course.CourseNotificationOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CourseNotificationOutboxRepository {

    private final CourseNotificationJpaRepository jpaRepository;

    public CourseNotificationOutbox save(final CourseNotificationOutbox notificationOutbox) {
        return jpaRepository.save(notificationOutbox);
    }

    public List<CourseNotificationOutbox> findOldestPendingByLessThanOrEqualTryCount(int size, int maxTryCount) {
        return jpaRepository.findPendingByLessThanOrEqualTryCount(
                PageRequest.of(0, size, Sort.by("createdAt")), maxTryCount);
    }
}
