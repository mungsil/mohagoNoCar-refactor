package com.example.mohago_nocar.global.common.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"longitude", "latitude"})
@ToString
public class Coordinate {

    private Double longitude; // x
    private Double latitude; // y

    public static Coordinate from(Double longitude, Double latitude) {
        return com.example.mohago_nocar.global.common.domain.vo.Coordinate.builder()
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }

    public static Coordinate from(String longitude, String latitude) {
        return com.example.mohago_nocar.global.common.domain.vo.Coordinate.from(Double.valueOf(longitude), Double.valueOf(latitude));
    }

}
