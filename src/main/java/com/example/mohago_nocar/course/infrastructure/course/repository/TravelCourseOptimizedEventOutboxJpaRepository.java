package com.example.mohago_nocar.course.infrastructure.course.repository;

import com.example.mohago_nocar.global.common.domain.OutboxStatus;
import com.example.mohago_nocar.course.domain.model.course.TravelCourseOptimizedEventOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TravelCourseOptimizedEventOutboxJpaRepository extends JpaRepository<TravelCourseOptimizedEventOutbox, Long> {

    @Query("SELECT o FROM TravelCourseOptimizedEventOutbox o " +
            "WHERE o.status IN :statuses " +
            "ORDER BY o.createdAt ASC " +
            "LIMIT :size")
    List<TravelCourseOptimizedEventOutbox> findTop10ByStatusInOrderByCreatedDateAsc(
            List<OutboxStatus> statuses, int size);

    @Modifying(clearAutomatically = true)
    @Query("update TravelCourseOptimizedEventOutbox o " +
            "set o.status = :targetStatus " +
            "where o.id in :ids")
    int updateStatuses(List<Long> ids, OutboxStatus targetStatus);

    @Modifying(clearAutomatically = true)
    @Query("update TravelCourseOptimizedEventOutbox o " +
          "set o.status = :targetStatus " +
            "where o.id = :id")
    int updateStatus(Long id, OutboxStatus targetStatus);

}
