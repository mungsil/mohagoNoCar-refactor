package com.example.mohago_nocar.transit.infrastructure.route;

import io.github.bucket4j.Bucket;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class RateLimitKey {

    private final Bucket bucket;
    private final String key;
    private final String encodedKey;

    public RateLimitKey(Bucket bucket, String key) {
        this.bucket = bucket;
        this.key = key;
        this.encodedKey = createEncodedApiKey(key);
    }

    private String createEncodedApiKey(String key) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8);
    }

    /**
     * API 호출 제한 속도에 맞추어 사용 가능한 Key를 반환합니다.
     * 호출 제한 속도를 초과하면 호출 스레드는 Blocking 됩니다.
     * @return API key
     */
    public String acquireEncodedKey() {
        try {
            bucket.asBlocking().consume(1);
        } catch (InterruptedException e) { // todo 인터럽트 발생 시 고민
            throw new RuntimeException(e);
        }

        return encodedKey;
    }

    public String getKey() {
        return key;
    }

}
