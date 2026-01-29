package com.example.mohago_nocar.course.infrastructure.course;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TravelCourseJpaRepository extends JpaRepository<TravelCourse, Long> {

    @Query("select u " +
            "from TravelCourse c " +
            "inner join AnonymousUser u " +
            "on u.id = c.anonymousUserId")
    Optional<AnonymousUser> findUserByCourseId(Long travelCourseId);

    @Query("select e " +
    "from TravelCourse c " +
    "inner join CourseOptimizedEvent e " +
    "on e.travelCourseId = c.id")
    Optional<CourseOptimizedEvent> findOptimizedEventByCourseId(Long travelCourseId);

}
