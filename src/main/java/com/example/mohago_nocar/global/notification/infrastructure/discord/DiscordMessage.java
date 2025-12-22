package com.example.mohago_nocar.global.notification.infrastructure.discord;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscordMessage {

    private final String content;

    // 필요 시 embeds 필드 추가...

    public static DiscordMessage of(String content) {
        return new DiscordMessage(content);
    }

}
