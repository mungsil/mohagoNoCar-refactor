package com.example.mohago_nocar.global.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Configuration
@Profile("test")
public class EmbeddedRedisConfig {

/*    @Value("${spring.data.redis.port")
    private int port;

    private RedisServer redisServer;

    @PostConstruct
    public void startEmbeddedRedis() {
        redisServer = RedisServer.builder()
                .port(port)
                .setting("maxmemory 128M")
                .build();
        redisServer.start();
    }

    @PreDestroy
    public void stopEmbeddedRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }*/

}
