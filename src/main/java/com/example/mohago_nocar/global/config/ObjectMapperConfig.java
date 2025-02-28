package com.example.mohago_nocar.global.config;

import com.example.mohago_nocar.transit.infrastructure.externalApi.odsay.ODsayApiResponseDeserializer;
import com.example.mohago_nocar.transit.infrastructure.externalApi.odsay.dto.response.OdsayRouteResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule()) // Java 8 날짜 지원
                .registerModule(new SimpleModule()
                        .addDeserializer(OdsayRouteResponse.class, new ODsayApiResponseDeserializer()))
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 포맷 유지
    }

}
