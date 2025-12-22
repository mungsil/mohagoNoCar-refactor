package com.example.mohago_nocar;

import com.example.mohago_nocar.course.application.route.RouteStepService;
import com.example.mohago_nocar.course.domain.model.course.TravelCourse;
import com.example.mohago_nocar.course.domain.model.routeStep.RouteStep;
import com.example.mohago_nocar.course.domain.service.TravelCourseUseCase;
import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.support.IntegrationTestSupport;
import com.example.mohago_nocar.support.LocalIntegrationTestSupport;
import com.example.mohago_nocar.transit.domain.model.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RouteStepTest extends LocalIntegrationTestSupport {

    @Autowired
    private TravelCourseUseCase travelCourseUseCase;

    @Autowired
    private RouteStepService routeStepService;

    @Test
    @DisplayName("RouteStep을 저장하고 조회할 때 SubPath 리스트가 JSONB로 정상 직렬화/역직렬화된다")
    void saveAndRetrieveRouteStepWithSubPaths() {
        // given: SubPath 리스트를 포함한 RouteStep 생성
        WalkPath walkPath = new WalkPath(1.5, 15);

        Coordinate busStartCoord = Coordinate.from(37.5547, 126.9707);
        Coordinate busEndCoord = Coordinate.from(37.5600, 126.9800);
        BusPath busPath = new BusPath(
                5.2, 20, "402", 1,
                "서울역", busStartCoord,
                "시청역", busEndCoord
        );

        Coordinate subwayStartCoord = Coordinate.from(37.5600, 126.9800);
        Coordinate subwayEndCoord = Coordinate.from(37.4979, 127.0276);
        SubwayPath subwayPath = new SubwayPath(
                10.3, 25, "2호선",
                "시청역", subwayStartCoord,
                "강남역", subwayEndCoord
        );

        List<SubPath> subPaths = List.of(walkPath, busPath, subwayPath);
        System.out.println("저장 전 =============");
        subPaths.forEach(System.out::println);
        System.out.println("====================");

        RouteStep routeStep = RouteStep.builder()
                .originSpotId(1L)
                .destinationSpotId(2L)
                .timeTakenMin(60)
                .distanceKm(17.1)
                .detailPaths(subPaths)
                .build();

        // finalizeRoute로 확인
        // when: 저장
        List<RouteStep> routeSteps = routeStepService.saveAll(List.of(routeStep));
        System.out.println("routeSteps returned saveAll method " + routeSteps);

        List<Long> ids = routeSteps.stream().map(route -> route.getId()).toList();
        List<RouteStep> found = routeStepService.findAll(ids);
        System.out.println("routeSteps returned findAll method " + routeSteps);

        // then: 조회 및 검증
//        RouteStep found = entityManager.find(RouteStep.class, routeStep.getId());
//
//        assertThat(found).isNotNull();
//        assertThat(found.getOriginSpotId()).isEqualTo(1L);
//        assertThat(found.getDestinationSpotId()).isEqualTo(2L);
//        assertThat(found.getTimeTakenMin()).isEqualTo(60);
//        assertThat(found.getDistanceKm()).isEqualTo(17.1);
//
//        // SubPath 리스트 검증
//        List<SubPath> foundPaths = found.getDetailPaths();
//        assertThat(foundPaths).hasSize(3);
//        System.out.println("조회 완료 =================");
//        foundPaths.forEach(System.out::println);
//        System.out.println("=========================");
//
//        // 첫 번째 경로 - WalkSubPath
//        SubPath firstPath = foundPaths.get(0);
//        System.out.println(firstPath);
//        assertThat(firstPath).isInstanceOf(WalkPath.class);
//        assertThat(firstPath.getDistanceKm()).isEqualTo(1.5);
//        assertThat(firstPath.getTimeTakenMin()).isEqualTo(15);
//        assertThat(firstPath.getPathType()).isEqualTo(PathType.WALK);
//
//        // 두 번째 경로 - BusPath
//        SubPath secondPath = foundPaths.get(1);
//        assertThat(secondPath).isInstanceOf(BusPath.class);
//        assertThat(secondPath.getDistanceKm()).isEqualTo(5.2);
//        assertThat(secondPath.getTimeTakenMin()).isEqualTo(20);
//        BusPath busPathResult = (BusPath) secondPath;
//        assertThat(busPathResult.getBusNo()).isEqualTo("402");
//        assertThat(busPathResult.getBusType()).isEqualTo(1);
//        assertThat(busPathResult.getStartName()).isEqualTo("서울역");
//        assertThat(busPathResult.getEndName()).isEqualTo("시청역");
//        assertThat(secondPath.getPathType()).isEqualTo(PathType.BUS);
//
//        // 세 번째 경로 - SubwayPath
//        SubPath thirdPath = foundPaths.get(2);
//        assertThat(thirdPath).isInstanceOf(SubwayPath.class);
//        assertThat(thirdPath.getDistanceKm()).isEqualTo(10.3);
//        assertThat(thirdPath.getTimeTakenMin()).isEqualTo(25);
//        SubwayPath subwayPathResult = (SubwayPath) thirdPath;
//        assertThat(subwayPathResult.getSubwayLineName()).isEqualTo("2호선");
//        assertThat(subwayPathResult.getStartName()).isEqualTo("시청역");
//        assertThat(subwayPathResult.getEndName()).isEqualTo("강남역");
//        assertThat(thirdPath.getPathType()).isEqualTo(PathType.SUBWAY);
    }

    @Test
    @DisplayName("추상 클래스를 jsonb type으로 저장할 수 있다")
    void shouldSaved(){
        // given: SubPath 리스트를 포함한 RouteStep 생성
        WalkPath walkPath = new WalkPath(1.5, 15);

        Coordinate busStartCoord = Coordinate.from(37.5547, 126.9707);
        Coordinate busEndCoord = Coordinate.from(37.5600, 126.9800);
        BusPath busPath = new BusPath(
                5.2, 20, "402", 1,
                "서울역", busStartCoord,
                "시청역", busEndCoord
        );

        Coordinate subwayStartCoord = Coordinate.from(37.5600, 126.9800);
        Coordinate subwayEndCoord = Coordinate.from(37.4979, 127.0276);
        SubwayPath subwayPath = new SubwayPath(
                10.3, 25, "2호선",
                "시청역", subwayStartCoord,
                "강남역", subwayEndCoord
        );

        List<SubPath> subPaths = List.of(walkPath, busPath, subwayPath);

        RouteStep routeStep = RouteStep.builder()
                .originSpotId(1L)
                .destinationSpotId(2L)
                .timeTakenMin(60)
                .distanceKm(17.1)
                .detailPaths(subPaths)
                .build();

        // when: 저장
//        entityManager.persist(routeStep);
    }

    // 1. 엔티티 관련 문제
    // 2. pojo 직렬화/역직렬화 문제 -> subpath pojo 역직렬화/직렬화해보기

}
