package com.example.mohago_nocar.course.application.course;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.domain.repository.CourseOptimizedEventRepository;
import com.example.mohago_nocar.global.common.domain.BaseEntity;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.support.LocalIntegrationTestSupport;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TravelCourseServiceTest extends LocalIntegrationTestSupport {

    @Autowired
    private TravelCourseService travelCourseService;

    @Autowired
    private CourseOptimizedEventRepository optimizedEventRepository;

    @Test
    @DisplayName("주어진 상태를 가지는 이벤트를 생성된 순으로 조회한다")
    void shouldReturnEventsWithGivenStatusOrderByCreatedAt(){
        //given
        int size = 10;
        List<EventProcessStatus> statuses = List.of(EventProcessStatus.PENDING_RETRY, EventProcessStatus.CREATED);
        AnonymousUser user = AnonymousUser.create("fcm token");
        // save events with the given status
        for (int i = 0; i < size; i++) {
            TravelCourse course = TravelCourse.create(user);
            course.setId((long)i);
            CourseOptimizedEvent optimizedEvent = CourseOptimizedEvent.create(user, course);
            optimizedEvent.setStatus(statuses.get(i%2));
            optimizedEventRepository.save(optimizedEvent);
        }

        // save an event with other status
        TravelCourse course = TravelCourse.create(user);
        course.setId(999L);
        CourseOptimizedEvent optimizedEvent = CourseOptimizedEvent.create(user, course);
        EventProcessStatus otherStatus = EventProcessStatus.FATAL_FAIL;
        optimizedEvent.setStatus(otherStatus);
        optimizedEventRepository.save(optimizedEvent);

        //when
        List<CourseOptimizedEvent> events = travelCourseService.getOldestOptimizedCourseEvents(size, statuses);

        //then
        assertEquals(size, events.size());
        // event를 생성된 시각을 기준으로 정렬한 리스트 생성
        List<CourseOptimizedEvent> orderedByCreatedAt = events.stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedAt))
                .toList();

        for (int i = 0; i < size; i++) {
            CourseOptimizedEvent event = events.get(i);
            EventProcessStatus status = event.getStatus();
            assertTrue(status.equals(EventProcessStatus.PENDING_RETRY) ||
                    status.equals(EventProcessStatus.CREATED));
            // 순서 비교
            assertEquals(event.getId(), orderedByCreatedAt.get(i).getId());
        }

    }

}