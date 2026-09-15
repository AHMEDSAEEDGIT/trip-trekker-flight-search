package com.apiintegration.flightsearch.infrastructure.provider.aviationstack.exception;

public class AviationstackMalformedResponseException extends RuntimeException {
    public AviationstackMalformedResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}