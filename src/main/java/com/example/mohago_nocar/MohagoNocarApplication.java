package com.example.mohago_nocar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MohagoNocarApplication {

	public static void main(String[] args) {
		SpringApplication.run(MohagoNocarApplication.class, args);
	}

}
