package com.example.mohago_nocar.plan.application.v2;

import com.example.mohago_nocar.transit.infrastructure.queue.batch.TransitRouteBatchUseCase;
import com.example.mohago_nocar.transit.infrastructure.queue.batch.TransitRouteBatchExecution;
import com.example.mohago_nocar.user.domain.AnonymousUser;
import com.example.mohago_nocar.user.domain.UserUseCase;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * [알림 전송 상황]
 * - plan 실패 시
 * - 대중교통 경로 조회 실패 시
 * - 대중교통 경로 조회 완료 시
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TravelCoursePlanNotifyService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserUseCase userUseCase;
    private final TransitRouteBatchUseCase transitRouteBatchUseCase;

    public void sendNotification(
            UUID userId,
            String title,
            String body,
            Long planId
    ) {
        try {
            AnonymousUser user = userUseCase.findByIdOrThrow(userId);

            Message message = Message.builder()
                    .putData("planId", String.valueOf(planId))
                    .setToken(user.getFcmToken())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body).build())
                    .build();

            log.info("메시지 전송을 시작합니다.");
            String response = firebaseMessaging.send(message);
            log.info("메시지 전송이 끝났습니다.");
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            log.error("FCM 메시지 전송 중 에러 발생: {}", e.getMessage());
            // todo 디스코드 알림 전송
        }
    }

    public void sendSuccessNotification(String batchId) {
        // 배치 조회
        TransitRouteBatchExecution execution = transitRouteBatchUseCase.getById(batchId);
        if (execution.isAlreadyDelivered()) {
            return;
        }

        Long planId = execution.getPlanId();
        UUID userId = execution.getUserId();
        String title = "오매~ 성공했어유";
        String body = "보러 오세요^^";

        try {
            AnonymousUser user = userUseCase.findByIdOrThrow(userId);

            Message message = Message.builder()
                    .putData("planId", String.valueOf(planId))
                    .setToken(user.getFcmToken())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body).build())
                    .build();

            String response = firebaseMessaging.send(message);
            execution.completeDeliver();

            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            log.error("FCM 메시지 전송 중 에러 발생: {}", e.getMessage());
            // todo 디스코드 알림 전송
        }
    }

    public void send(String token) {

        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle("안녕하세유")
                        .setBody("음음...").build())
                .build();

        try {
            log.info("메시지 전송을 시작합니다.");
            String response = firebaseMessaging.send(message);
            log.info("메시지 전송이 끝났습니다: {}", response);

        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
