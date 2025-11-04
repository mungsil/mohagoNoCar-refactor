package com.example.mohago_nocar.transit.infrastructure.route.batch;

import com.example.mohago_nocar.transit.infrastructure.route.TransitRouteApiAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class TransitRouteBatchConfig {


    @Bean
    TransitRouteBatchLauncher transitRouteBatchLauncher(RedisTemplate<String, Object> redisTemplateWithObj) {
        return new TransitRouteBatchLauncher(transitRouteItemProducer(redisTemplateWithObj));
    }

    @Bean
    TransitRouteItemProducer transitRouteItemProducer(RedisTemplate<String, Object> redisTemplateWithObj) {
        return new TransitRouteItemProducer(redisTemplateWithObj);
    }

    @Bean
    TransitRouteItemConsumer transitRouteItemConsumer(RedisTemplate<String, Object> redisTemplateWithObj,
                                                      TransitRouteApiAdapter transitRouteApiAdapter) {
        return new TransitRouteItemConsumer(redisTemplateWithObj, transitRouteApiAdapter);
    }

}
