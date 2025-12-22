package com.example.mohago_nocar.course.domain.model.course;

public enum TravelCourseCompletionMessage {

    SUCCESS("여행 계획 완성! ✈️", "여행 계획 설계가 완료되었어요. 탭해서 확인해보세요!"),
    FAILURE("여행 계획 수립 실패...", "예상치 못한 에러로 계획 수립에 실패했어요.");

    private final String title;
    private final String body;

    TravelCourseCompletionMessage(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public static TravelCourseCompletionMessage fromResult(boolean result) {
        return result ? SUCCESS : FAILURE;
    }

}
