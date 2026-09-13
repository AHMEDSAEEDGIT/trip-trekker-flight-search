package com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception;

public class FlightApiTimeoutException extends RuntimeException{
    public FlightApiTimeoutException(String message) { super(message); }
}
