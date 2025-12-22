package com.example.mohago_nocar.global.notification.infrastructure.discord;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class DiscordMessageSender {

    @Value("${discord.webhook-url}")
    private String webhookUrl;
    private WebClient webClient;

    @PostConstruct
    public void init() {
        webClient = WebClient.builder().build();
    }

    public void send(DiscordMessage message) {
        try {
            webClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(message)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Discord 메시지 전송 성공: content={}", message.getContent());
        } catch (Exception e) {
            log.error("Discord 메시지 전송 실패: {}", e.getMessage(), e);
        }
    }

}
