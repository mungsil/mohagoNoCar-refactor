package com.example.mohago_nocar.transit.infrastructure.error.code;

import com.example.mohago_nocar.global.common.exception.GlobalStatus;
import com.example.mohago_nocar.global.common.exception.InternalServerException;
import com.example.mohago_nocar.global.common.exception.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OdsayErrorCode implements Status {

    // SERVER_ERROR
    ODSAY_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ODSAY500", "ODsay 서버에 오류가 발생하였습니다. ODsay API를 확인해주세요."),

    // BAD_REQUEST
    REQUIRED_INPUT_FORMAT_ERROR(HttpStatus.BAD_REQUEST, "ODSAY400", "필수 입력값 형식 및 범위 오류입니다."),
    REQUIRED_INPUT_MISSING(HttpStatus.BAD_REQUEST, "ODSAY400", "필수 입력값이 누락되었습니다."),
    POINTS_WITHIN_DISTANCE(HttpStatus.BAD_REQUEST, "ODSAY400", "출, 도착지가 700m 이내이므로 길찾기 정보가 제공되지 않습니다."),
    COMPONENT_ERROR(HttpStatus.BAD_REQUEST, "ODSAY400" , "잘못된 요청입니다."),

    // NOT_FOUND
    STARTING_POINT_MISSING(HttpStatus.NOT_FOUND, "ODSAY404", "출발지 정류장이 없습니다."),
    ARRIVAL_POINT_MISSING(HttpStatus.NOT_FOUND, "ODSAY404", "도착지 정류장이 없습니다."),
    START_ARRIVAL_POINTS_MISSING(HttpStatus.NOT_FOUND, "ODSAY404", "출발지, 도착지 정류장이 없습니다."),
    SERVICE_AREA_NOT_AVAILABLE(HttpStatus.NOT_FOUND, "ODSAY404", "서비스 지역이 아닙니다."),
    NO_SEARCH_RESULTS(HttpStatus.NOT_FOUND, "ODSAY404", "길찾기 검색결과가 없습니다."),

    // TOO_MANY_REQUESTS
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "ODSAY429", "Too Many Requests"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public static OdsayErrorCode from(String code) {
        return switch (code) {
            case "500" -> ODSAY_SERVER_ERROR;
            case "-8" -> REQUIRED_INPUT_FORMAT_ERROR;
            case "-9" -> REQUIRED_INPUT_MISSING;
            case "3" -> STARTING_POINT_MISSING;
            case "4" -> ARRIVAL_POINT_MISSING;
            case "5" -> START_ARRIVAL_POINTS_MISSING;
            case "6" -> SERVICE_AREA_NOT_AVAILABLE;
            case "-98" -> POINTS_WITHIN_DISTANCE;
            case "-99" -> NO_SEARCH_RESULTS;
            case "-1" -> COMPONENT_ERROR;
            case "429" -> TOO_MANY_REQUESTS;
            default -> throw new InternalServerException("unknown Error Code 발생 : "+ code);
        };
    }

    public boolean isDistanceError() {
        return this == POINTS_WITHIN_DISTANCE;
    }

    public boolean isServerError() {
        return this == ODSAY_SERVER_ERROR;
    }

    public boolean isTooManyRequests() {
        return this == TOO_MANY_REQUESTS;
    }

    public boolean isUnExpectedError() {
        return this == REQUIRED_INPUT_FORMAT_ERROR || this == REQUIRED_INPUT_MISSING || this == COMPONENT_ERROR;
    }

}
