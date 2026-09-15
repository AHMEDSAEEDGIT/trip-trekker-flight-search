package com.apiintegration.flightsearch.infrastructure.provider.aviationstack.exception;

public class AviationstackUnauthorizedException extends RuntimeException {
    public AviationstackUnauthorizedException(String message) {
        super(message);
    }
}