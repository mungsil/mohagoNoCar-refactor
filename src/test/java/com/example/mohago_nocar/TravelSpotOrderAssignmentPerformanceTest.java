package com.example.mohago_nocar;

import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.plan.domain.model.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class TravelSpotOrderAssignmentPerformanceTest {
    @Test
    @DisplayName("방식1(Map 인덱싱)과 방식2(중첩 루프) 성능 비교 - 5개 장소")
    void comparePerformanceWith5Spots() {
        // Given: 5개의 TravelSpot (일부 중복 좌표 포함)
        Set<TestTravelSpot> spots1 = createTestSpots(100);
        Set<TestTravelSpot> spots2 = new HashSet<>(spots1);

        List<Coordinate> optimalRoute = spots1.stream()
                .map(s -> s.getLocation().getCoordinate())
                .collect(Collectors.toList());

        // When & Then: 방식1 (Map 인덱싱)
        long startTime1 = System.nanoTime();
        assignOrderWithMap(spots1, optimalRoute);
        long endTime1 = System.nanoTime();
        long duration1 = endTime1 - startTime1;

        // When & Then: 방식2 (중첩 루프)
        long startTime2 = System.nanoTime();
        assignOrderWithNestedLoop(spots2, optimalRoute);
        long endTime2 = System.nanoTime();
        long duration2 = endTime2 - startTime2;

        // 결과 검증
        assertThat(spots1).allMatch(s -> s.getVisitOrder() != null);
        assertThat(spots2).allMatch(s -> s.getVisitOrder() != null);

        // 성능 비교 출력
        System.out.println("=== 100개 장소 성능 비교 ===");
        System.out.println("방식1 (Map 인덱싱): " + duration1 + " ns (" + duration1/1000.0 + " μs)");
        System.out.println("방식2 (중첩 루프): " + duration2 + " ns (" + duration2/1000.0 + " μs)");
        System.out.println("속도 차이: " + (duration2 > duration1 ?
                "방식1이 " + (duration2 - duration1) + " ns 더 빠름" :
                "방식2가 " + (duration1 - duration2) + " ns 더 빠름"));
    }

    @RepeatedTest(10)
    @DisplayName("반복 테스트로 평균 성능 측정 - 5개 장소")
    void repeatedPerformanceTest() {
        Set<TestTravelSpot> spots1 = createTestSpots(5);
        Set<TestTravelSpot> spots2 = new HashSet<>(spots1);

        List<Coordinate> optimalRoute = spots1.stream()
                .map(s -> s.getLocation().getCoordinate())
                .collect(Collectors.toList());

        long startTime1 = System.nanoTime();
        assignOrderWithMap(spots1, optimalRoute);
        long duration1 = System.nanoTime() - startTime1;

        long startTime2 = System.nanoTime();
        assignOrderWithNestedLoop(spots2, optimalRoute);
        long duration2 = System.nanoTime() - startTime2;

        System.out.printf("방식1: %d ns | 방식2: %d ns | 차이: %d ns%n",
                duration1, duration2, Math.abs(duration1 - duration2));
    }

    @Test
    @DisplayName("중복 좌표가 있는 경우 테스트")
    void testWithDuplicateCoordinates() {
        // Given: 중복 좌표 포함
        Coordinate dupCoord = Coordinate.from(127.0, 37.5);
        Set<TestTravelSpot> spots = new HashSet<>();
        spots.add(new TestTravelSpot(1L, null, new Location(dupCoord, "장소1")));
        spots.add(new TestTravelSpot(1L, null, new Location(dupCoord, "장소2")));
        spots.add(new TestTravelSpot(1L, null, new Location(Coordinate.from(127.1, 37.6), "장소3")));

//        List<Coordinate> route = Arrays.asList(
//                Coordinate.from(127.1, 37.6),
//                dupCoord
//        );

        List<Coordinate> route = Arrays.asList(
                dupCoord
        );

        // When
        Set<TestTravelSpot> spots1 = new HashSet<>(spots);
        Set<TestTravelSpot> spots2 = new HashSet<>(spots);

        assignOrderWithMap(spots1, route);

        // Then: 중복 좌표의 경우 Map 방식은 마지막 것만, 중첩 루프는 모두 처리
        System.out.println("\n=== 중복 좌표 처리 결과 ===");
        System.out.println("방식1 (Map): " );
        spots1.stream()
                .forEach(s -> System.out.println(s.getLocation().name + ": "+ s.getVisitOrder()));

        assignOrderWithNestedLoop(spots2, route);
        System.out.println("방식2 (중첩 루프): " );
        spots2.stream()
                .forEach(s -> System.out.println(s.getLocation().name + ": "+ s.getVisitOrder()));
    }

    @Test
    @DisplayName("정확성 검증 - 순서가 올바르게 할당되는지 확인")
    void verifyCorrectness() {
        // Given
        Set<TestTravelSpot> spots = createTestSpots(5);
        List<Coordinate> route = spots.stream()
                .map(s -> s.getLocation().getCoordinate())
                .collect(Collectors.toList());

        Set<TestTravelSpot> spots1 = new HashSet<>(spots);
        Set<TestTravelSpot> spots2 = new HashSet<>(spots);

        // When
        assignOrderWithMap(spots1, route);
        assignOrderWithNestedLoop(spots2, route);

        // Then
        List<Integer> orders1 = spots1.stream()
                .map(TestTravelSpot::getVisitOrder)
                .sorted()
                .collect(Collectors.toList());

        List<Integer> orders2 = spots2.stream()
                .map(TestTravelSpot::getVisitOrder)
                .sorted()
                .collect(Collectors.toList());

        assertThat(orders1).containsExactly(0, 1, 2, 3, 4);
        assertThat(orders2).containsExactly(0, 1, 2, 3, 4);
    }

    // 방식1: Map 인덱싱
    private void assignOrderWithMap(Set<TestTravelSpot> unOrderedTravelSpots, List<Coordinate> optimalRoute) {
        Map<Coordinate, List<TestTravelSpot>> index = unOrderedTravelSpots.stream()
                .collect(Collectors.groupingBy(s -> s.getLocation().getCoordinate()));

        int order = 0;
        for (Coordinate next : optimalRoute) {
            List<TestTravelSpot> spots = index.get(next);
            if (spots != null) {
                for (TestTravelSpot spot : spots) {
                    spot.setOrder(order++);
                }
            }
        }
    }


    // 방식2: 중첩 루프
    private void assignOrderWithNestedLoop(Set<TestTravelSpot> unOrderedTravelSpots, List<Coordinate> optimalRoute) {
        int order = 0;
        for (Coordinate nxt : optimalRoute) {
            for (TestTravelSpot travelSpot : unOrderedTravelSpots) {
                Coordinate spotCoordi = travelSpot.getLocation().getCoordinate();
                if (spotCoordi.equals(nxt)) {
                    travelSpot.setOrder(order++);
                }
            }
        }
        if (order != optimalRoute.size()) {
            throw new RuntimeException("모든 장소에 순서가 부여되지 못했거나 여행 장소의 경위도 외의 추가 경위도가 존재합니다.");
        }
    }

    // 테스트용 데이터 생성
    private Set<TestTravelSpot> createTestSpots(int count) {
        Set<TestTravelSpot> spots = new HashSet<>();
        for (int i = 0; i < count; i++) {
            Coordinate coord = Coordinate.from(127.0 + i * 0.1, 37.5 + i * 0.1);
            spots.add(new TestTravelSpot(1L, null, new Location(coord, "장소" + i)));
        }
        return spots;
    }

    // 테스트용 TravelSpot 구현
    static class TestTravelSpot {
        private Long id;
        private Long courseId;
        private Integer visitOrder;
        private Location location;

        public TestTravelSpot(Long courseId, Integer visitOrder, Location location) {
            this.courseId = courseId;
            this.visitOrder = visitOrder;
            this.location = location;
        }

        public void setOrder(int i) {
            visitOrder = i;
        }

        public Integer getVisitOrder() {
            return visitOrder;
        }

        public Location getLocation() {
            return location;
        }

        @Override
        public String toString() {
            return "TestTravelSpot{" +
                    "id=" + id +
                    ", courseId=" + courseId +
                    ", visitOrder=" + visitOrder +
                    ", location=" + location +
                    '}';
        }
    }

    static class Location {
        private Coordinate coordinate;
        private String name;

        public Location(Coordinate coordinate, String name) {
            this.coordinate = coordinate;
            this.name = name;
        }

        public Coordinate getCoordinate() {
            return coordinate;
        }

        @Override
        public String toString() {
            return "Location{" +
                    "coordinate=" + coordinate +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

}
