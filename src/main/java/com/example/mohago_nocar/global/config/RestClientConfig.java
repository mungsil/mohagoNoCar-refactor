package com.example.mohago_nocar.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

@Configuration
public class RestClientConfig {

    @Value("${spring.threads.virtual.enabled}")
    private boolean isVirtualThreadEnabled;

    @Bean
    public RestClient restClient() {
        var httpRequestFactory = createHttpRequestFactory();
        httpRequestFactory.setReadTimeout(Duration.ofSeconds(10));

        return RestClient.builder()
                .requestFactory(httpRequestFactory)
                .build();
    }

    private JdkClientHttpRequestFactory createHttpRequestFactory() {
        if (isVirtualThreadEnabled) {
            return new JdkClientHttpRequestFactory(HttpClient.newBuilder()
                    .executor(Executors.newVirtualThreadPerTaskExecutor())
                    .build());
        }

        return new JdkClientHttpRequestFactory();
    }

}
