package com.example.mohago_nocar.course.infrastructure.course;

import com.example.mohago_nocar.course.domain.model.course.CourseOptimizedEvent;
import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseOptimizedEventJpaRepository extends JpaRepository<CourseOptimizedEvent, Long> {

    @Query("select e " +
            "from CourseOptimizedEvent e " +
            "where e.status in :eventProcessStatus " +
            "order by e.createdAt asc " +
            "limit :size")
    List<CourseOptimizedEvent> findTopNByStatusInOrderByCreatedDateAsc(int size, List<EventProcessStatus> eventProcessStatus);

}
