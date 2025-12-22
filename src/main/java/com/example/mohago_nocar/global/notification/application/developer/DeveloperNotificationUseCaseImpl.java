package com.example.mohago_nocar.global.notification.application.developer;

import com.example.mohago_nocar.global.util.ObjectMapperUtil;
import com.example.mohago_nocar.global.notification.infrastructure.discord.DiscordMessage;
import com.example.mohago_nocar.global.notification.infrastructure.discord.DiscordMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DeveloperNotificationUseCaseImpl implements DeveloperNotificationUseCase {

    private final DiscordMessageSender discordMessageSender;
    private final ObjectMapperUtil objectMapperUtil;

    @Override
    public void sendNotification(String content) {
        discordMessageSender.send(DiscordMessage.of(content));
    }

    @Override
    public void sendNotification(String contentTitle, Throwable throwable) {
        String content = makeSummaryContentInJson(contentTitle, throwable);
        DiscordMessage discordMessage = DiscordMessage.of(content);
        discordMessageSender.send(discordMessage);
    }

    private String makeSummaryContentInJson(String msgTitle, Throwable throwable) {
        String errorMessage = throwable.getMessage();

        // 스택 트레이스 상위 5줄 추출
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
            sb.append(stackTrace[i].toString()).append("\n");
        }

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("title", msgTitle);
        payloadMap.put("errorMessage", errorMessage);
        payloadMap.put("stackTrace", sb.toString());

        String content = objectMapperUtil.writeValue(payloadMap);
        return content;
    }

}
