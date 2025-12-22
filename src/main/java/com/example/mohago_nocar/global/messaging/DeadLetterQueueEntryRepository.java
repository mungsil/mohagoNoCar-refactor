package com.example.mohago_nocar.global.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeadLetterQueueEntryRepository extends JpaRepository<DeadLetterQueueEntry, Long> {

    List<DeadLetterQueueEntry> findByStatus(DLQStatus status);

    long countByStatus(DLQStatus status);

    /**
     * 예외 타입별 통계
     */
    @Query("SELECT d.exceptionType, COUNT(d) FROM DeadLetterQueueEntry d " +
            "GROUP BY d.exceptionType ORDER BY COUNT(d) DESC")
    List<Object[]> countByExceptionType();

    /**
     * 특정 Stream의 엔트리 조회
     */
    List<DeadLetterQueueEntry> findByStreamKeyAndStatus(
            String streamKey,
            DLQStatus status
    );

    /**
     * 오래된 NEW 상태 엔트리 (알림용)
     */
    @Query("SELECT d FROM DeadLetterQueueEntry d " +
            "WHERE d.status = 'NEW' " +
            "AND d.createdAt < :threshold " +
            "ORDER BY d.createdAt ASC")
    List<DeadLetterQueueEntry> findOldNewEntries(@Param("threshold") LocalDateTime threshold);

    Optional<DeadLetterQueueEntry> findByEntryId(String entryId);

    List<DeadLetterQueueEntry> findAllByEntryIdIn(List<String> entryIds);

}
