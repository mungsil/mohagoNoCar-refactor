package com.example.mohago_nocar.transit.infrastructure.route.odsay;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Getter
@ConfigurationProperties("odsay")
public class OdsayApiProperties {

    private final String requestUrl;
    private final String key;
    private final String encodedKey;
    private final int executionIntervalMills;

    public OdsayApiProperties(String requestUrl, String key, int executionIntervalMills) {
        this.requestUrl = requestUrl;
        this.key = key;
        this.encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
        this.executionIntervalMills = executionIntervalMills;
    }

}
