package com.example.mohago_nocar.global.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
public class TimeZoneConfig {

    @PostConstruct
    public void configTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

}
