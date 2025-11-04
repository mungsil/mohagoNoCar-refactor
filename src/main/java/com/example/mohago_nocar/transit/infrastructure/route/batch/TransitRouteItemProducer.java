package com.example.mohago_nocar.transit.infrastructure.route.batch;

import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.domain.model.OdsayApiRequest;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;

import java.util.ArrayList;
import java.util.List;

public class TransitRouteItemProducer {

    public static final String ODSAY_API_REQUEST_STREAM_NAME = "odsay-api-request";

    private final RedisTemplate<String, Object> redisTemplateWithObj;

    public TransitRouteItemProducer(RedisTemplate<String, Object> redisTemplateWithObj) {
        this.redisTemplateWithObj = redisTemplateWithObj;
    }

    public void produce(String batchId, List<Location> locations) {
        validateLocations(locations);
        List<OdsayApiRequest> items = createItems(batchId, locations);
        submit(items);
    }

    private void submit(List<OdsayApiRequest> items) {
        for (OdsayApiRequest item : items) {
            ObjectRecord<String, OdsayApiRequest> apiReq = StreamRecords.newRecord()
                    .in(ODSAY_API_REQUEST_STREAM_NAME)
                    .ofObject(item);

            redisTemplateWithObj
                    .opsForStream(new Jackson2HashMapper(true))
                    .add(apiReq);
        }
    }

    private List<OdsayApiRequest> createItems(String batchId, List<Location> locations) {
        List<OdsayApiRequest> odsayApiRequests = new ArrayList<>();

        for (int i = 1; i < locations.size(); i++) {
            Location origin = locations.get(i-1);
            Location destination = locations.get(i);

            OdsayApiRequest apiRequest = OdsayApiRequest.of(batchId, origin, destination, i);
            odsayApiRequests.add(apiRequest);
        }

        return odsayApiRequests;
    }

    private void validateLocations(List<Location> locations) {
        if (locations == null || locations.size() < 2) {
            throw new IllegalArgumentException("최소 2개 이상의 위치가 필요합니다.");
        }
    }

}
