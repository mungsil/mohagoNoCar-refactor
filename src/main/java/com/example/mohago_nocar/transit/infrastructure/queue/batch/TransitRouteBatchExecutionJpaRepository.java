package com.example.mohago_nocar.transit.infrastructure.queue.batch;

import org.springframework.data.repository.CrudRepository;

public interface TransitRouteBatchExecutionJpaRepository extends CrudRepository<TransitRouteBatchExecution, String> {
}
