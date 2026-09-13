package com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception;

public class FlightApiException  extends RuntimeException {
    public FlightApiException(String message) { super(message); }
    public FlightApiException(String message, Throwable cause) { super(message, cause); }
}