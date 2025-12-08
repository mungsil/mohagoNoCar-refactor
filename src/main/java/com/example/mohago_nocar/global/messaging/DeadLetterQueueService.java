package com.example.mohago_nocar.global.messaging;

import com.example.mohago_nocar.course.infrastructure.stream.DeadLetterQueueEntryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeadLetterQueueService {

    private final DeadLetterQueueEntryRepository dlqRepository;

    @Transactional
    public void save(DeadLetterQueueEntryDto dto) {
        dlqRepository.save(DeadLetterQueueEntry.create(dto));
    }

    /**
     * 주어진 DTO 리스트를 기반으로 {@link DeadLetterQueueEntry} 목록을 저장합니다.
     *
     * <p>저장 동작은 다음 기준에 따라 처리됩니다:</p>
     * <ul>
     *     <li>동일한 {@code recordId}의 엔트리가 이미 존재하는 경우
     *         → 내용이 동일하면 업데이트 시각만 갱신하고, 다르면 새로운 엔트리를 추가로 저장합니다.
     *     </li>
     *     <li>{@code recordId}가 존재하지 않는 경우
     *         → 새로운 엔트리를 생성하여 저장합니다.
     *     </li>
     * </ul>
     *
     * @param newDeadEntries 재시도 불가능한 예외로 인해 처리되지 못한 엔트리를 나타내는 DTO 리스트
     * @return 저장된 {@link DeadLetterQueueEntry} 리스트
     */
    @Transactional
    public List<DeadLetterQueueEntry> saveAll(List<DeadLetterQueueEntryDto> newDeadEntries) {
        List<String> entryIds = newDeadEntries.stream()
                .map(DeadLetterQueueEntryDto::getId)
                .toList();

        Map<String, DeadLetterQueueEntry> existedMap = dlqRepository.findAllByEntryIdIn(entryIds).stream()
                .collect(Collectors.toMap(
                        DeadLetterQueueEntry::getEntryId,
                        Function.identity()
                ));

        List<DeadLetterQueueEntry> entries = newDeadEntries.stream()
                .map(DeadLetterQueueEntry::create)
                .map(newEntry -> {
                    String entryId = newEntry.getEntryId();
                    DeadLetterQueueEntry existingEntry = existedMap.get(entryId);
                    if (existingEntry != null) {
                        existingEntry.syncUpdateTime(LocalDateTime.now());
                        return existingEntry;
                    }else {
                        return newEntry;
                    }
                }).toList();

        log.info("Saving dead letter queue entries");
        log.info("Dead letter queue entries: {}", entries);

        return dlqRepository.saveAll(entries);
    }

    @Transactional(readOnly = true)
    public List<DeadLetterQueueEntry> findByStatus(DLQStatus status) {
        return dlqRepository.findByStatus(status);
    }

    @Transactional
    public void changeStatus(Long entryId, DLQStatus newStatus) {
        DeadLetterQueueEntry entry = dlqRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("DLQ entry not found: " + entryId));

        entry.changeStatus(newStatus);
        dlqRepository.save(entry);

        log.info("DLQ entry status changed: id={}, status={}",
                entryId, newStatus);
    }

}
