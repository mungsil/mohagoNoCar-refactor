package com.example.mohago_nocar.transit.infrastructure.queue.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransitRouteBatchService implements TransitRouteBatchUseCase {

    private final TransitRouteBatchExecutionRepository repository;

    @Override
    public TransitRouteBatchExecution createAndSaveExecution(int totalPlaceCount, UUID userId, Long planId) {
        TransitRouteBatchExecution entity = TransitRouteBatchExecution.createInitial(totalPlaceCount, userId, planId);
        return repository.save(entity);
    }

    @Override
    public TransitRouteBatchExecution getById(String executionId) {
        return repository.findByExecutionId(executionId);
    }

}
