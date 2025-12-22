package com.example.mohago_nocar.test;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DistinctTimeTest {

    @Test
    @DisplayName("나노초 단위로 when 절 수행 시간 측정")
    void testMakingDistinctTime(){
        //given
        // Coordinate 클래스와 createDestinations() 메서드는 필요합니다.
        List<Coordinate> destinations = createDestinations();

        //when
        long startNanoTime = System.nanoTime(); // when 절 시작 시간 기록 (나노초)
        Map<Coordinate, Set<Object>> map = new HashMap<>();

        for (Coordinate coordinate : destinations) {
            map.compute(coordinate, (k, v) -> {
                if (v == null) {
                    // 첫 번째 요소는 식별자 "coordinate1"로 유지
                    return new HashSet<>(Set.of("coordinate1"));
                } else {
                    // Key가 있으면 기존 Set에 현재 나노초 시간 추가
                    v.add(System.nanoTime());
                    return v;
                }
            });
        }

        long endNanoTime = System.nanoTime(); // when 절 종료 시간 기록 (나노초)
        long totalDurationNanos = endNanoTime - startNanoTime; // 총 수행 시간 (나노초)

        // 나노초 단위의 시간을 보기 좋은 포맷으로 변환 및 출력
        long seconds = totalDurationNanos / 1_000_000_000; // 초 (10^9 나노초)
        long nanoseconds = totalDurationNanos % 1_000_000_000; // 나머지 나노초

        System.out.println("---");
        // 초와 나머지 나노초를 함께 출력합니다. (%09d는 나노초를 9자리로 채워서 출력)
        System.out.printf("when 절 수행 시간: %d.%09d초 (%d ns)%n",
                seconds, nanoseconds, totalDurationNanos);
        System.out.println("---");

        //then
        for (Map.Entry<Coordinate, Set<Object>> entry : map.entrySet()) {
            Coordinate coordinate = entry.getKey();
            Set<Object> objects = entry.getValue();
            System.out.println(coordinate);
            System.out.println(objects);
        }
    }

    private Coordinate createFixedCoordinate() {
        return Coordinate.from(126.872939584803, 37.3700357495453);
    }

    private List<Coordinate> createDestinations() {
        Coordinate dest1 = Coordinate.from(126.8834795656736, 37.351812431636645);
        Coordinate dest2 = Coordinate.from(126.899445340496, 37.3673238473972);
        Coordinate dest3 = Coordinate.from(126.848208105819, 37.3649832880928);
        Coordinate dest4 = createFixedCoordinate();
        Coordinate dest5 = createFixedCoordinate();

        return List.of(dest1, dest2, dest3, dest4, dest5);
    }

}
