package com.apiintegration.flightsearch.api.error;

import com.apiintegration.flightsearch.infrastructure.provider.duffel.exception.DuffelApiException;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.exception.DuffelAuthenticationException;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.exception.DuffelTimeoutException;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.exception.DuffelUnauthorizedException;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception.FlightApiDownException;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception.FlightApiException;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception.FlightApiMalformedResponseException;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception.FlightApiTimeoutException;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception.FlightApiUnauthorizedException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiError> handleBadRequest(Exception exception) {
        return response(HttpStatus.BAD_REQUEST, "Invalid flight search request");
    }

    @ExceptionHandler({FlightApiUnauthorizedException.class, DuffelUnauthorizedException.class,
            DuffelAuthenticationException.class})
    public ResponseEntity<ApiError> handleProviderUnauthorized(Exception exception) {
        return response(HttpStatus.BAD_GATEWAY, "Flight provider authentication failed");
    }

    @ExceptionHandler({FlightApiTimeoutException.class, DuffelTimeoutException.class})
    public ResponseEntity<ApiError> handleProviderTimeout(Exception exception) {
        return response(HttpStatus.GATEWAY_TIMEOUT, "Flight provider request timed out");
    }

    @ExceptionHandler({FlightApiDownException.class})
    public ResponseEntity<ApiError> handleFlightApiDown(FlightApiDownException exception) {
        return response(HttpStatus.BAD_GATEWAY, "FlightAPI is unavailable");
    }

    @ExceptionHandler({FlightApiException.class, FlightApiMalformedResponseException.class, DuffelApiException.class})
    public ResponseEntity<ApiError> handleProviderFailure(Exception exception) {
        return response(HttpStatus.BAD_GATEWAY, "Flight provider request failed");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Flight search failed");
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiError(message));
    }
}
