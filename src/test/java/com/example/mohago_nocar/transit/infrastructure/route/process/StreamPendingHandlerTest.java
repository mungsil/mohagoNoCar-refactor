package com.example.mohago_nocar.transit.infrastructure.route.process;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StreamPendingHandlerTest {

    @Test
    @DisplayName("비정상적으로 오래 걸리는 경우 서버 크러쉬로 간주하고 재처리를 시도한다")
    void t1(){
        //given
        // 요청 전송

        //when
        // 파트 1인데 10초 걸려
        // 파트 2인데 타임아웃 시간보다 더 걸려
        // -> 결과 저장되어 있으면 알림 전송 후 ack처리할지도? -> 알림이 이미 전송되어있으면 어카냐고요

        //then
        // 재시도큐로 전송됨
    }

    @Test
    @DisplayName("API 응답 타임아웃 에러가 발생한 경우 실패 처리한다")
    void t2(){
        //given

        //when

        //then

    }

    @Test
    @DisplayName("실패 표기된 배치 발견 시 실패 알림을 전송한다")
    void t3(){
        //given

        //when

        //then

    }

    @Test
    @DisplayName("외부 API 서버 내부 원인인 경우 에러 메시지를 로깅한다")
    void t4(){
        //given

        //when

        //then

    }

}