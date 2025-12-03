package com.example.mohago_nocar.transit.infrastructure.route.odsay;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Set;

@Getter
@ConfigurationProperties("odsay")
public class OdsayApiKeyProperties {

    private final List<String> keys;

    public OdsayApiKeyProperties(List<String> apiKeys) {
        if (Set.of(apiKeys.toArray()).size() != apiKeys.size()) {
            throw new RuntimeException("API 키는 중복일 수 없습니다.");
        }

        this.keys = apiKeys;
    }

}
