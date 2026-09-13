package com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception;

public class FlightApiMalformedResponseException extends RuntimeException {
    public FlightApiMalformedResponseException(String message, Throwable cause) { super(message, cause); }
}