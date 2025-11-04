package com.example.mohago_nocar.transit.infrastructure.route.batch;

import com.example.mohago_nocar.transit.domain.model.TransitRouteBatchExecution;
import org.springframework.data.repository.CrudRepository;

public interface TransitRouteBatchExecutionJpaRepository extends CrudRepository<TransitRouteBatchExecution, String> {
}
