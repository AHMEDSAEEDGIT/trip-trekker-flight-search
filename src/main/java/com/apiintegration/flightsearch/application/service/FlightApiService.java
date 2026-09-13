package com.apiintegration.flightsearch.application.service;

import com.apiintegration.flightsearch.api.dto.request.FlightSearchRequest;
import com.apiintegration.flightsearch.api.dto.request.Passenger;
import com.apiintegration.flightsearch.application.port.FlightSearchProvider;
import com.apiintegration.flightsearch.domain.model.FlightOffer;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.FlightApiClient;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.FlightApiResponseMapper;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.dto.FlightApiResponseWrapper;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception.FlightApiMalformedResponseException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class FlightApiService implements FlightSearchProvider {
    private final FlightApiClient flightApiClient;
    private final FlightApiResponseMapper responseMapper;
    private final ObjectMapper objectMapper;

    public FlightApiService(FlightApiClient flightApiClient,
                            FlightApiResponseMapper responseMapper,
                            ObjectMapper objectMapper) {
        this.flightApiClient = flightApiClient;
        this.responseMapper = responseMapper;
        this.objectMapper = objectMapper;
    }

    public List<FlightOffer> searchFlights(FlightSearchRequest request) {
        int adults = countPassengersByType(request.passengers(), "ADULT", 1);
        int children = countPassengersByType(request.passengers(), "CHILD", 0);
        int infants = countPassengersByType(request.passengers(), "INFANT", 0);
        String cabinClass = request.cabinClass() != null
            ? toProviderCabinClass(request.cabinClass().name())
            : "Economy";
        String currency = "USD";

        // 1. Call provider
        String rawJson = flightApiClient.searchOneWayTrip(
                request.origin(),
                request.destination(),
                request.departureDate().toString(),
                adults,
                children,
                infants,
                cabinClass,
                currency
        );

        // 2. Parse raw JSON into wrapper DTO (Handles malformed response)
        FlightApiResponseWrapper responseWrapper;
        try {
            responseWrapper = objectMapper.readValue(rawJson, FlightApiResponseWrapper.class);
            return responseMapper.mapToDomain(responseWrapper, currency);
        } catch (Exception e) {
            throw new FlightApiMalformedResponseException("Invalid or malformed response from FlightAPI", e);
        }
    }

    private int countPassengersByType(List<Passenger> passengers, String type, int defaultCount) {
        if (passengers == null || passengers.isEmpty()) {
            return defaultCount;
        }
        return (int) passengers.stream()
                .filter(p -> p.type() != null && p.type().name().equalsIgnoreCase(type))
                .count();
    }

    private String toProviderCabinClass(String cabinClass) {
        return switch (cabinClass) {
            case "ECONOMY" -> "Economy";
            case "PREMIUM_ECONOMY" -> "Premium_Economy";
            case "BUSINESS" -> "Business";
            case "FIRST" -> "First";
            default -> cabinClass;
        };
    }
}
