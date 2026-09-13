package com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception;

public class FlightApiUnauthorizedException extends RuntimeException{
    public FlightApiUnauthorizedException(String message) { super(message); }
}
