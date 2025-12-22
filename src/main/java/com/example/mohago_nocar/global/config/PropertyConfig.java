package com.example.mohago_nocar.global.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource("classpath:env.properties")
})
@ConfigurationPropertiesScan(basePackages = {"com.example.mohago_nocar"})
public class PropertyConfig {
}
