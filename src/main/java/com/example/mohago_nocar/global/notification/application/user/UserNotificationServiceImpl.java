package com.example.mohago_nocar.global.notification.application.user;

import com.example.mohago_nocar.global.notification.infrastructure.fcm.FcmMessage;
import com.example.mohago_nocar.global.notification.infrastructure.fcm.FcmMessageSender;
import com.example.mohago_nocar.user.domain.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private final FcmMessageSender fcmMessageSender;
    private final UserUseCase userUseCase;

    @Override
    public String send(UserNotificationDto dto) { // todo notnull validation
        Objects.requireNonNull(dto);

        String fcmToken = userUseCase.getFcmToken(dto.getUserId());
        FcmMessage fcmMessage = FcmMessage.create(dto.getTitle(), dto.getBody(), fcmToken, dto.getCustomData());

        return fcmMessageSender.send(fcmMessage);
    }

}
