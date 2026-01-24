package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TravelCourseOptimizedEventJpaRepository extends JpaRepository<TravelCourseOptimizedEvent, Long> {

    @Query("SELECT o FROM TravelCourseOptimizedEvent o " +
            "WHERE o.status IN :statuses " +
            "ORDER BY o.createdAt ASC " +
            "LIMIT :size")
    List<TravelCourseOptimizedEvent> findTop10ByStatusInOrderByCreatedDateAsc(
            List<EventProcessStatus> statuses, int size);

    @Modifying(clearAutomatically = true)
    @Query("update TravelCourseOptimizedEvent o " +
            "set o.status = :targetStatus " +
            "where o.id in :ids")
    int updateStatuses(List<Long> ids, EventProcessStatus targetStatus);

    @Modifying(clearAutomatically = true)
    @Query("update TravelCourseOptimizedEvent o " +
          "set o.status = :targetStatus " +
            "where o.id = :id")
    int updateStatus(Long id, EventProcessStatus targetStatus);

}
