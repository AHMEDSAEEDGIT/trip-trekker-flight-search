package com.apiintegration.flightsearch.infrastructure.provider.aviationstack.dto;

public record AviationstackFlightSearchRequest(
        String origin,
        String destination,
        String flightDate,
        String flightStatus,
        Integer limit) {
}