package com.example.mohago_nocar.course.infrastructure.course;

import com.example.mohago_nocar.course.domain.model.course.CourseNotificationOutbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseNotificationJpaRepository extends JpaRepository<CourseNotificationOutbox, Long> {

    @Query("select n " +
            "from CourseNotificationOutbox n " +
            "where n.isSuccess = false " +
            "and n.tryCount <= :maxTryCount")
    List<CourseNotificationOutbox> findPendingByLessThanOrEqualTryCount(Pageable pageable, Integer maxTryCount);

}
