package com.example.mohago_nocar.transit.infrastructure.queue.batch;

import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.infrastructure.queue.producer.TransitRouteRequestProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 대중교통 경로를 구하는 배치 작업을 진입 순서대로 시작합니다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TransitRouteBatchLauncher {

    private final ReentrantLock lock = new ReentrantLock(true);
    private final TransitRouteRequestProducer itemProducer;

    public void launch(TransitRouteBatchExecution execution, List<Location> locations) {
        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                itemProducer.produce(execution.getId(), locations);
            }else {
                throw new RuntimeException("Failed to acquire lock within timeout");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

}
