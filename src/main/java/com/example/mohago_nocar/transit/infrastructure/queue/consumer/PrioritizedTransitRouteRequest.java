package com.example.mohago_nocar.transit.infrastructure.queue.consumer;

import com.example.mohago_nocar.transit.infrastructure.queue.batch.TransitRouteRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrioritizedTransitRouteRequest implements Comparable<PrioritizedTransitRouteRequest> {

    private Long insertionOrder; // tie-breaker: 동일 우선순위 시, 먼저 들어온 요청이 우선됨
    private TransitRouteRequest value;

    @Override
    public int compareTo(PrioritizedTransitRouteRequest o) {
        int compared = this.value.compareTo(o.getValue());
        if (compared != 0) {
            return compared;
        }

        // 기존 값의 우선 순위가 동일한 경우 진입 순서 활용
        if (this.insertionOrder < o.insertionOrder) {
            return -1;
        }
        if (this.insertionOrder > o.insertionOrder) {
            return 1;
        }

        return 0;
    }

}
