package com.example.mohago_nocar.support;

import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.festival.domain.model.vo.ActivePeriod;
import com.example.mohago_nocar.global.common.domain.vo.Coordinate;
import com.example.mohago_nocar.place.domain.model.Place;
import com.example.mohago_nocar.place.domain.model.PlaceCategory;
import com.example.mohago_nocar.plan.domain.model.Location;
import com.example.mohago_nocar.transit.domain.model.RouteMetrics;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.dto.response.GoogleDistanceMatrixResponse;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class Fixtures {

    static Faker faker = new Faker(Locale.KOREA);

    public static List<Place> places(int numberOfPlaces) {
        return IntStream.range(0, numberOfPlaces).mapToObj(i -> place()).toList();
    }

    private static Place place() {
        return Place.from(
                faker.number().digits(4), // 랜덤 ID
                faker.company().name() + " " + faker.options().option("해수욕장", "공원", "시장", "온천", "미술관"), // 장소 이름
                Coordinate.from(
                        faker.number().randomDouble(8, 126, 130), // 한국 경도 범위
                        faker.number().randomDouble(8, 34, 38)    // 한국 위도 범위
                ),
                faker.address().fullAddress(),
                faker.internet().url(),
                faker.options().option(PlaceCategory.values())
        );
    }

    public static Festival festival() {
        LocalDate startDate = LocalDate.now().minusDays(faker.number().numberBetween(1, 14));
        LocalDate endDate = LocalDate.now().plusDays(faker.number().numberBetween(1, 7));

        return Festival.from(
                faker.expression(faker.name() + "축제"), // 자연스러운 축제 이름
                ActivePeriod.from(startDate, endDate),
                faker.lorem().sentence(8) + " 🎉",                // 랜덤 설명
                faker.address().state(),                          // 지역 (예: 강원도, 전라남도)
                Coordinate.from(
                        faker.number().randomDouble(8, 126, 130),
                        faker.number().randomDouble(8, 34, 38)
                )
        );
    }

/*    public static List<RouteMetrics> googleApiResponse() {
        Coordinate c1 = Coordinate.from(126.872939584803, 37.3700357495453);
        Coordinate c2 = Coordinate.from(126.8834795656736, 37.351812431636645);
        Coordinate c3 = Coordinate.from(126.903150731652, 37.366183537311);
        Coordinate c4 = Coordinate.from(126.848208105819, 37.3649832880928);
        Coordinate c5 = Coordinate.from(126.897131767179, 37.3693104219685);

        return List.of(
                // origin: c1
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("2.769 km", 2769L),
                                new GoogleDistanceMatrixResponse.Duration("24분", 1440L)),
                        c1, c2),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("18.793 km", 18793L),
                                new GoogleDistanceMatrixResponse.Duration("122분", 7320L)),
                        c1, c3),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("5.39 km", 5390L),
                                new GoogleDistanceMatrixResponse.Duration("55분", 3300L)),
                        c1, c4),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("2.555 km", 2555L),
                                new GoogleDistanceMatrixResponse.Duration("38분", 2280L)),
                        c1, c5),

                // origin: c2
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("2.762 km", 2762L),
                                new GoogleDistanceMatrixResponse.Duration("24분", 1440L)),
                        c2, c1),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("12.709 km", 12709L),
                                new GoogleDistanceMatrixResponse.Duration("104분", 6240L)),
                        c2, c3),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("7.724 km", 7724L),
                                new GoogleDistanceMatrixResponse.Duration("73분", 4380L)),
                        c2, c4),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("4.563 km", 4563L),
                                new GoogleDistanceMatrixResponse.Duration("53분", 3180L)),
                        c2, c5),

                // origin: c3
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("20.308 km", 20308L),
                                new GoogleDistanceMatrixResponse.Duration("101분", 6060L)),
                        c3, c1),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("19.463 km", 19463L),
                                new GoogleDistanceMatrixResponse.Duration("109분", 6540L)),
                        c3, c2),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("22.867 km", 22867L),
                                new GoogleDistanceMatrixResponse.Duration("107분", 6420L)),
                        c3, c4),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("13.475 km", 13475L),
                                new GoogleDistanceMatrixResponse.Duration("99분", 5940L)),
                        c3, c5),

                // origin: c4
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("5.152 km", 5152L),
                                new GoogleDistanceMatrixResponse.Duration("54분", 3240L)),
                        c4, c1),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("7.827 km", 7827L),
                                new GoogleDistanceMatrixResponse.Duration("73분", 4380L)),
                        c4, c2),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("23.228 km", 23228L),
                                new GoogleDistanceMatrixResponse.Duration("109분", 6540L)),
                        c4, c3),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("7.253 km", 7253L),
                                new GoogleDistanceMatrixResponse.Duration("85분", 5100L)),
                        c4, c5),

                // origin: c5
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("2.831 km", 2831L),
                                new GoogleDistanceMatrixResponse.Duration("37분", 2220L)),
                        c5, c1),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("4.568 km", 4568L),
                                new GoogleDistanceMatrixResponse.Duration("54분", 3240L)),
                        c5, c2),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("12.748 km", 12748L),
                                new GoogleDistanceMatrixResponse.Duration("96분", 5760L)),
                        c5, c3),
                RouteMetrics.of(new GoogleDistanceMatrixResponse.Element(
                                new GoogleDistanceMatrixResponse.Distance("8.514 km", 8514L),
                                new GoogleDistanceMatrixResponse.Duration("69분", 4140L)),
                        c5, c4)
        );
    }*/

    public static List<Location> locations(int numOfLocations) {
        return IntStream.range(0, numOfLocations)
                .mapToObj(i -> location())
                .toList();
    }

    public static Location location() {
        return Location.of(
                faker.funnyName().name(),
                Coordinate.from(
                        faker.number().randomDouble(8, 126, 130),
                        faker.number().randomDouble(8, 34, 38)
                ));
    }

}
