package com.example.mohago_nocar.transit.infrastructure.route;

import com.example.mohago_nocar.transit.infrastructure.route.odsay.OdsayApiKeyProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitedApiKeyPoolConfig {

    @Bean
    public RateLimitedApiKeyPool rateLimitedOdsayApiKeyPool(OdsayApiKeyProperties keyProperties) {
        return new RateLimitedApiKeyPool(keyProperties, this::createBucket);
    }

    private Bucket createBucket() {
        Bandwidth limit = BandwidthBuilder.builder().capacity(5)
                .refillIntervally(5, Duration.ofSeconds(1))
                .initialTokens(0)
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

}
