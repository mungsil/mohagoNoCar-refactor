package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.course.application.dto.GetRequesterInfoDto;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TravelCourseJpaRepository extends JpaRepository<TravelCourse, Long> {

    @Query("""
        SELECT new com.example.mohago_nocar.course.application.dto.GetRequesterInfoDto(
            au.id,
            au.fcmToken
        )
        FROM TravelCourse tc
        JOIN AnonymousUser au ON tc.anonymousUserId = au.id
        WHERE tc.id = :travelCourseId
        """)
    Optional<GetRequesterInfoDto> findRequesterInfoByTravelCourseId(@Param("travelCourseId") Long travelCourseId);

    @Query("SELECT tc FROM TravelCourse tc " +
            "WHERE tc.createdAt <= :thresholdTime " +
            "AND tc.notificationSent = :notificationSent")
    List<TravelCourse> findOutdatedCoursesNeedingNotification(
            @Param("thresholdTime") LocalDateTime thresholdTime,
            @Param("notificationSent") Boolean notificationSent);

}
