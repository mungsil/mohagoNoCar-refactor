package com.example.mohago_nocar.transit.infrastructure.route.batch;

import com.example.mohago_nocar.transit.domain.model.TransitRouteBatchExecution;
import com.example.mohago_nocar.transit.domain.repository.TransitRouteBatchProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class TransitRouteBatchProgressRepositoryImpl implements TransitRouteBatchProgressRepository {

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
