package com.example.mohago_nocar.global.config;

import com.example.mohago_nocar.transit.infrastructure.externalApi.odsay.ODsayApiResponseDeserializer;
import com.example.mohago_nocar.transit.infrastructure.externalApi.odsay.dto.response.OdsayRouteResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder googleWebClient() {
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(googleHttpClient()));
    }

    @Bean
    public WebClient.Builder odsayWebClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper())))
                .clientConnector(new ReactorClientHttpConnector(odsayHttpClient()));
    }

    @Bean
    public HttpClient googleHttpClient() {
        return createHttpClient(5000, 10, 10);
    }

    @Bean
    public HttpClient odsayHttpClient() {
        return createHttpClient(5000, 10, 10);
    }


    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SimpleModule()
                .addDeserializer(OdsayRouteResponse.class, new ODsayApiResponseDeserializer()));
        return objectMapper;
    }

    private HttpClient createHttpClient(
            int connectTimeoutMilis,
            int readTimeoutSeconds,
            int writeTimeoutSeconds
    ) {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMilis)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeoutSeconds))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeoutSeconds)));
    }

}
