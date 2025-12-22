package com.example.mohago_nocar.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ForCustomLoggerTest {

    ForCustomLogger logger = new ForCustomLogger();

    @Test
    @DisplayName("로그를 찍어봐요")
    void test(){
        //given

        //when
        logger.log();

        //then

    }

}