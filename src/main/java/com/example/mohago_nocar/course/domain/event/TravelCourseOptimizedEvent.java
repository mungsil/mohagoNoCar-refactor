package com.example.mohago_nocar.course.domain.event;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * 여행 코스에 포함된 여행 장소들을 방문할 순서의 최적화 완료 이벤트
 */
@Getter
@ToString
public class TravelCourseOptimizedEvent {

    private Long travelCourseId;
    private UUID anonymousUserId;

    public static TravelCourseOptimizedEvent of(Long travelCourseId, UUID anonymousUserId) {
        return new TravelCourseOptimizedEvent(travelCourseId, anonymousUserId);
    }

    private TravelCourseOptimizedEvent(Long travelCourseId, UUID anonymousUserId) {
        this.travelCourseId = travelCourseId;
        this.anonymousUserId = anonymousUserId;
    }

}
