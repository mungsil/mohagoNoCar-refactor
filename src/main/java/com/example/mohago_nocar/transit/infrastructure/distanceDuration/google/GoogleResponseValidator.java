package com.example.mohago_nocar.transit.infrastructure.distanceDuration.google;

import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.dto.response.GoogleDistanceMatrixResponse;
import com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.dto.response.GoogleDistanceMatrixStatus;

import java.util.List;

public class GoogleResponseValidator {

    public static boolean hasError(GoogleDistanceMatrixResponse response) {
        return isInvalidStatus(response) || containsNullElements(response.rows());
    }

    private static boolean isInvalidStatus(GoogleDistanceMatrixResponse response) {
        return response.status() != GoogleDistanceMatrixStatus.OK;
    }

    private static boolean containsNullElements(List<GoogleDistanceMatrixResponse.Row> elements) {
        if (elements == null) {
            return true;
        }

        return elements.get(0).elements().stream()
                .anyMatch(element ->
                        element.distance() == null || element.distance().text() == null ||
                                element.duration() == null || element.duration().text() == null
                );
    }

}
