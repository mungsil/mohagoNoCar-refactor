package com.example.mohago_nocar.transit.infrastructure.queue.producer;

import com.example.mohago_nocar.global.util.ObjectMapperUtil;
import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.infrastructure.queue.batch.TransitRouteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TransitRouteRequestProducer {

    @Value("${redis.streams.odsay.main}")
    private String streamKey;

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final ObjectMapperUtil objectMapperUtil;

    public void produce(String batchId, List<Location> locations) {
        validateLocations(locations);
        List<TransitRouteRequest> items = createItems(batchId, locations);
        submit(items);
    }

    private void submit(List<TransitRouteRequest> items) {
        for (TransitRouteRequest item : items) {
            ObjectRecord<String, String> apiReq = StreamRecords.newRecord()
                    .in(streamKey)
                    .ofObject(objectMapperUtil.writeValue(item));

            stringRedisTemplate.opsForStream().add(apiReq);
        }
    }

    private List<TransitRouteRequest> createItems(String batchId, List<Location> locations) {
        List<TransitRouteRequest> transitRouteRequests = new ArrayList<>();

        for (int i = 1; i < locations.size(); i++) {
            Location origin = locations.get(i-1);
            Location destination = locations.get(i);

            TransitRouteRequest apiRequest = TransitRouteRequest.of(batchId, origin, destination, i);
            transitRouteRequests.add(apiRequest);
        }

        return transitRouteRequests;
    }

    private void validateLocations(List<Location> locations) {
        if (locations == null || locations.size() < 2) {
            throw new IllegalArgumentException("최소 2개 이상의 위치가 필요합니다.");
        }
    }

}
