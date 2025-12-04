package com.example.mohago_nocar.transit.infrastructure.queue.batch;

import java.util.UUID;

public interface TransitRouteBatchUseCase {

    TransitRouteBatchExecution createAndSaveExecution(int totalPlaceCount, UUID userId, Long planId);

    TransitRouteBatchExecution getById(String executionId);

}
