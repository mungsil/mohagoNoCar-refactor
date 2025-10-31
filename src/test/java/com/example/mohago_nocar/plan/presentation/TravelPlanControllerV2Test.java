package com.example.mohago_nocar.plan.presentation;

import com.example.mohago_nocar.plan.presentation.v2.TravelPlanControllerV2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@WebMvcTest(controllers = TravelPlanControllerV2.class)
class TravelPlanControllerV2Test {
    
    @Test
    @DisplayName("요청 파라미터가 유효하다면 성공 응답을 받는다.")
    void shouldReturnSuccessResponseIfRequestIsValid(){
        //given
        
        //when
        
        //then
        
    }

    @Test
    @DisplayName("요청 파라미터가 유효하지 않다면 실패 응답을 받는다.")
    void shouldReturnFailureResponseIfRequestIsInvalid(){
        //given

        //when

        //then

    }

}