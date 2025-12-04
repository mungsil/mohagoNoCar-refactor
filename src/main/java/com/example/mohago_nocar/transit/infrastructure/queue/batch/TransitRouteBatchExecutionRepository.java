package com.example.mohago_nocar.transit.infrastructure.queue.batch;

public interface TransitRouteBatchExecutionRepository {

    TransitRouteBatchExecution save(TransitRouteBatchExecution execution);

    TransitRouteBatchExecution findByExecutionId(String executionId);

}
