package com.apiintegration.flightsearch.infrastructure.provider.duffel.exception;

public class DuffelAuthenticationException extends RuntimeException {
    public DuffelAuthenticationException() {
        super("Duffel rejected the access token. Check DUFFEL_ACCESS_TOKEN.");
    }
}