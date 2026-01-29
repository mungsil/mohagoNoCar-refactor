package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.domain.model.course.CourseNotificationOutbox;
import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.domain.repository.CourseOptimizedEventRepository;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.course.infrastructure.course.CourseNotificationOutboxRepository;
import com.example.mohago_nocar.support.LocalIntegrationTestSupport;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseNotificationServiceTest extends LocalIntegrationTestSupport {

    @Autowired
    private CourseNotificationService courseNotificationService;

    @Autowired
    private CourseNotificationOutboxRepository notificationOutboxRepository;

    @Test
    @DisplayName("일정 횟수 이하로 전송을 시도한 미처리 알림을 오래된 순으로 조회한다")
    void shouldReturnOldestPendingNotifications(){
        //given
        int size = 10;
        int maxTryNum = 3;
        // create unprocessed notifications
        for (int i = 0; i < size; i++) {
            CourseNotificationOutbox outbox = CourseNotificationOutbox.create((long) i);
            notificationOutboxRepository.save(outbox);
        }
        // create processed notification
        CourseNotificationOutbox processedOutbox = CourseNotificationOutbox.create(999L);
        processedOutbox.markSuccess();
        notificationOutboxRepository.save(processedOutbox);

        //when
        List<CourseNotificationOutbox> notificationOutBoxes = courseNotificationService.getOldestPendingNotificationOutBoxes(size, maxTryNum);

        //then
        assertEquals(size, notificationOutBoxes.size());
        for (int i = 0; i < size; i++) {
            CourseNotificationOutbox outbox = notificationOutBoxes.get(i);
            assertTrue(outbox.getTryCount() <= maxTryNum);
            assertEquals(outbox.getTravelCourseId(), i);
        }
    }

}