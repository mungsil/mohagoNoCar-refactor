package com.example.mohago_nocar.transit.infrastructure.distanceDuration.google.dto.response;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GoogleDistanceMatrixStatus {

    OK("valid result"),
    INVALID_REQUEST("provided request was invalid"),
    MAX_ELEMENTS_EXCEEDED("product of origins and destinations exceeds the per-query limit"),
    MAX_DIMENSIONS_EXCEEDED("number of origins or destinations exceeds the per-query limit"),
    OVER_DAILY_LIMIT("any of the following: 1. The API key is missing or invalid | 2. Billing has not been enabled on your account | 3. self-imposed usage cap has been exceeded | 4. The provided method of payment is no longer valid"),
    OVER_QUERY_LIMIT("service has received too many requests from your application within the allowed time period"),
    REQUEST_DENIED("service denied use of the Distance Matrix service by your application"),
    UNKNOWN_ERROR("Distance Matrix request could not be processed due to a server error");

    private final String message;

}
