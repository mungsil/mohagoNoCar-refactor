package com.example.mohago_nocar.transit;

import com.example.mohago_nocar.support.IntegrationTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

// key:batchId, value: route 저장
// batch의 completedTaskCount+1 update
// batch가 완료되었다면 batch status 변경
// batch entry를 ack + delete
// batch status 반환
public class LuaScriptTest extends IntegrationTestSupport {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String ZSET_KEY = "lua:zset:test";

    @Test
    @DisplayName("응답 정보를 저장할 sorted set을 사용해본다")
    void shouldUseSortedSet(){
        //given
        String lua =
                "local key = KEYS[1]\n" +
                        "local score = tonumber(ARGV[1])\n" +
                        "local member = ARGV[2]\n" +
                        "return redis.call('ZADD', key, score, member)";

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(lua, Long.class);
        Long result = redisTemplate.execute(script, List.of(ZSET_KEY), "10", "coffee");

        assertThat(result).isEqualTo(1L);
        assertThat(redisTemplate.opsForZSet().range(ZSET_KEY, 0, -1)).isEqualTo(Set.of("coffee"));

        //when

        //then

    }

    @Test
    @DisplayName("배치 정보가 저장된 해시를 사용해본다")
    void shouldUseHash(){
        //given
        String lua =
                "local key = KEYS[1]\n" +
                        "local count = tonumber(ARGV[1])\n" +
                        "local lastId = ARGV[2]\n" +
                        "return redis.call('XREAD', 'COUNT', count, 'STREAMS', key, lastId)";

        DefaultRedisScript<List> script = new DefaultRedisScript<>(lua, List.class);

        //when
        List mystream = redisTemplate.execute(script, List.of("mystream"), "2", "0");

        //then
        System.out.println(mystream);
    }

    @Test
    @DisplayName("스트림 엔트리에 ack과 delete를 사용해본다")
    void shouldUseStream(){
        //given

        //when

        //then

    }

}
