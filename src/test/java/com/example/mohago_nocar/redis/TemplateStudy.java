package com.example.mohago_nocar.redis;

import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.support.Fixtures;
import com.example.mohago_nocar.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class TemplateStudy extends IntegrationTestSupport {

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Object> objectRedisTemplate;

    @Test
    @DisplayName("<String, String>이 뭐야")
    void whatIsStringString(){
        //given

        //when
        stringRedisTemplate.opsForValue().set("hello", "world");
        stringRedisTemplate.opsForList().leftPush("hello2", "world");
        stringRedisTemplate.opsForList().leftPush("hello2", "hello");

        //then
        System.out.println(stringRedisTemplate.opsForValue().get("hello"));
        System.out.println(stringRedisTemplate.opsForList().range("hello2", 0, 2));
    }

    @Test
    @DisplayName("<String, Object>가 뭐야")
    void whatIsStringObject(){
        //given
        //when
        Location location = Fixtures.location();
        objectRedisTemplate.opsForValue().set("hello", location);

        //then
        System.out.println(objectRedisTemplate.opsForValue().get("hello"));
    }

}
