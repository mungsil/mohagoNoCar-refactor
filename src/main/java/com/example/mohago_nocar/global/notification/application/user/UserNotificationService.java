package com.example.mohago_nocar.global.notification.application.user;

public interface UserNotificationService {

    String send(UserNotificationDto userNotificationDto);

}

/**
 *
 * <타 도메인에서 사용 시>
 *  - NotifyAdapter.sendToFcm, sendToDiscord = NotifyService.send
 *  - 어댑터에서 Fcm, discord 메시지 빌드
 *  - 타 도메인은 '안내 메시지'를 정의하는 책임
 *
 * NotifyService [interface] --> make easy testing
 * NotifyServiceImpl
 * send(fcmMsgDto)
 * send(discordMsgDto)
 * - 알림 도메인은 '전송하는 방법' 책임
 *
 * [단점]
 * - fcm, discord 등 구현체를 알아야함
 * - 근데 구현체를 알아야하지 않나? 이게 단점인가?
 */

/**
 * [타 도메인]
 * - Adapter interface
 *      send
 * - TravelCourseAdapter
 *      send:
 *      1. converter.convertToFcm
 *      2. service.send
 * - RetryableTravelCourseAdapter
 *    Retry retry;
 *      send:
 *          retryDecorate(() -> TravelCourseAdapter.send())
 *
 *
 * [알람 도메인]
 * - service.sendFcm(FcmMsg)
 * - service.sendFcm(fcmMsg, retry)
 * - service.sendDiscord(DiscorMsg)
 *
 */