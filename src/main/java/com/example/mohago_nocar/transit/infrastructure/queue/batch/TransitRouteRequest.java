package com.example.mohago_nocar.transit.infrastructure.queue.batch;

import com.example.mohago_nocar.plan.domain.model.Location;
import lombok.*;

import java.util.UUID;

// todo Comparable 제거
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class TransitRouteRequest implements Comparable<TransitRouteRequest> {

    String id;
    String batchId;
    Location origin;
    Location destination;
    Integer sequence;
    Integer retry;
    Boolean needRetry;

    public Integer plusRetry() {
        return ++retry;
    }

    public void markNeedRetry() {
        needRetry = true;
    }

    public static TransitRouteRequest of(String batchId, Location origin, Location destination, Integer sequence) {
        String id = UUID.randomUUID().toString();

        return TransitRouteRequest.builder()
                .id(id)
                .batchId(batchId)
                .origin(origin)
                .destination(destination)
                .sequence(sequence)
                .build();
    }

    @Builder
    private TransitRouteRequest(String id, String batchId, Location origin, Location destination, Integer sequence) {
        this.id = id;
        this.batchId = batchId;
        this.origin = origin;
        this.destination = destination;
        this.sequence = sequence;
        this.retry = 0;
        this.needRetry = false;
    }

    @Override
    public int compareTo(TransitRouteRequest o) {
        if (this.retry > o.getRetry()) {
            return -1;
        }

        if (this.retry < o.getRetry()) {
            return 1;
        }

        return 0;
    }

}
