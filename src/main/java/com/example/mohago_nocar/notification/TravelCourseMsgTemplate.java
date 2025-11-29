package com.example.mohago_nocar.notification;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

// 이건...... course 도메인에 둬도 되지 않을까
@ToString(callSuper = true)
@Getter
@NoArgsConstructor
public class TravelCourseMsgTemplate extends FcmMsgTemplate {

    public static class SuccessTemplate {
        private static final String TITLE = "여행 계획 완성! ✈️";
        private static final String BODY = "여행 계획 설계가 완료되었어요. 탭해서 확인해보세요!";
    }

    public static class FailureTemplate {
        private static final String TITLE = "여행 계획 수립 실패...";
        private static final String BODY = "예상치 못한 에러로 계획 수립에 실패했어요.";
    }

    Long travelCourseId;

    UUID userId;

    Boolean isSuccess;

    public static TravelCourseMsgTemplate create(
            boolean isSuccess, long travelCourseId, @NotNull UUID userId) {
        String title = isSuccess ? SuccessTemplate.TITLE : FailureTemplate.TITLE;
        String body = isSuccess ? SuccessTemplate.BODY : FailureTemplate.BODY;

        return TravelCourseMsgTemplate.builder()
                .title(title)
                .body(body)
                .isSuccess(isSuccess)
                .travelCourseId(travelCourseId)
                .userId(userId)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private TravelCourseMsgTemplate(
            String title, String body, Long travelCourseId, UUID userId, Boolean isSuccess) {
        super(title, body);
        this.travelCourseId = travelCourseId;
        this.userId = userId;
        this.isSuccess = isSuccess;
    }

    /**
     * Returns custom data as a map of field names to string values.
     *
     * <p><b>Notice:</b> This implementation uses hard‑coded field names.
     * If any field names change in the class, this method must be updated manually.
     *
     * @return map containing travelCourseId, userId, and isSuccess values
     */
    @Override
    Map<String, String> getAllCustomData() {
        Objects.requireNonNull(userId);

        return Map.of(
                "travelCourseId", String.valueOf(travelCourseId),
                "userId", userId.toString(),
                "isSuccess", String.valueOf(isSuccess)
        );
    }

}
