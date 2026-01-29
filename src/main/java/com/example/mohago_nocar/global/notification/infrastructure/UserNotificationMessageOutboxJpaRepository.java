package com.example.mohago_nocar.global.notification.infrastructure;

import com.example.mohago_nocar.global.common.domain.EventProcessStatus;
import com.example.mohago_nocar.global.notification.domain.UserNotificationMessageOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserNotificationMessageOutboxJpaRepository extends JpaRepository<UserNotificationMessageOutbox, Long> {

    @Query(value = "select o from UserNotificationMessageOutbox o " +
            "WHERE o.status IN :statuses " +
            "ORDER BY o.createdAt ASC " +
            "LIMIT :size ")
    List<UserNotificationMessageOutbox> findByStatusInOrderByCreatedDateAsc(
            List<EventProcessStatus> statuses, int size
    );

}
