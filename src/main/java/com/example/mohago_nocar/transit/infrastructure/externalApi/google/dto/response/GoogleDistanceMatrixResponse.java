package com.example.mohago_nocar.transit.infrastructure.externalApi.google.dto.response;

import java.util.List;

public record GoogleDistanceMatrixResponse(
        List<String> destination_addresses,
        List<String> origin_addresses,
        List<Row> rows
) {

    public record Row(
            List<Element> elements
    ){}

    public record Element(
            Distance distance,
            Duration duration
    ){}

    public record Distance(
            String text,
            Long value
    ){}

    public record Duration(
            String text,
            Long value
    ){}

}
