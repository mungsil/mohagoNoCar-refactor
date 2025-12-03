package com.example.mohago_nocar.transit.infrastructure.route;

import com.example.mohago_nocar.transit.infrastructure.route.odsay.OdsayApiKeyProperties;
import io.github.bucket4j.Bucket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class RateLimitedApiKeyPool {

    private final List<RateLimitKey> keys;
    private AtomicInteger index;

    public RateLimitedApiKeyPool(
            OdsayApiKeyProperties keyProperties, Supplier<Bucket> bucketCreator) {
        keys = new ArrayList<>();
        index = new AtomicInteger(0);
        for (String key : keyProperties.getKeys()) {
            RateLimitKey rateLimitKey = new RateLimitKey(bucketCreator.get(), key);
            keys.add(rateLimitKey);
        }
    }

    /**
     * 인코딩된 API key를 획득합니다.
     * API 호출 시 429 Too Many Request Error가 발생하지 않도록 key 획득 속도를 조절합니다.
     * @return
     */
    public String acquireEncodedKey() {
        // 원자적 + 라운드로빈 방식으로 인덱스 획득
        int idx = index.getAndUpdate(current -> (current + 1) % keys.size());
        RateLimitKey rateLimitKey = keys.get(idx);
        return rateLimitKey.acquireEncodedKey();
    }

    public int getNextOrder() {
        return index.get();
    }

}
