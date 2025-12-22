package com.example.mohago_nocar.support;

import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@TestConfiguration
public class FakeDataFactoryConfig {

    @Bean
    public Faker faker() {
        return new Faker(new Locale("ko"));
    }

}
