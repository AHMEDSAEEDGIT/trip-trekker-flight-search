package com.apiintegration.flightsearch.infrastructure.provider.aviationstack.exception;

public class AviationstackTimeoutException extends RuntimeException {
    public AviationstackTimeoutException(String message) {
        super(message);
    }
}