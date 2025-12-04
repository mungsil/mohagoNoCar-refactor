package com.example.mohago_nocar.transit.infrastructure.queue.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class TransitRouteBatchExecutionRepositoryImpl implements TransitRouteBatchExecutionRepository {

    private final TransitRouteBatchExecutionJpaRepository jpaRepository;

    @Override
    public TransitRouteBatchExecution save(TransitRouteBatchExecution execution) {
        return jpaRepository.save(execution);
    }

    @Override
    public TransitRouteBatchExecution findByExecutionId(String executionId) {
        return jpaRepository.findById(executionId).orElse(null);
    }

}
