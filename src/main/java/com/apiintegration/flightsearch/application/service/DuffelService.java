package com.apiintegration.flightsearch.application.service;

import com.apiintegration.flightsearch.api.dto.request.FlightSearchRequest;
import com.apiintegration.flightsearch.application.port.FlightSearchProvider;
import com.apiintegration.flightsearch.domain.model.FlightOffer;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.DuffelClient;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.DuffelResponseMapper;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.dto.DuffelOfferRequestPayload;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.dto.DuffelResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DuffelService implements FlightSearchProvider {
    private final DuffelClient duffelClient;
    private final DuffelResponseMapper duffelResponseMapper;
    private final ObjectMapper objectMapper; // Spring Boot's built-in JSON parser

    public List<FlightOffer> searchFlights(FlightSearchRequest request) {
        DuffelOfferRequestPayload payload = DuffelOfferRequestPayload.builder()
            .data(DuffelOfferRequestPayload.DataWrapper.builder()
                .slices(List.of(
                    DuffelOfferRequestPayload.Slice.builder()
                        .origin(request.origin())
                        .destination(request.destination())
                        .departureDate(request.departureDate().toString())
                        .build()
                ))
                .passengers(toDuffelPassengers(request))
                .cabinClass(request.cabinClass() != null ? request.cabinClass().name().toLowerCase() : "economy")
                .build())
            .build();

        String rawJsonResponse = duffelClient.createOfferRequest(payload);
        DuffelResponseWrapper responseWrapper;
        try {
            responseWrapper = objectMapper.readValue(rawJsonResponse, DuffelResponseWrapper.class);
        } catch (JacksonException e) {
            throw new com.apiintegration.flightsearch.infrastructure.provider.duffel.exception.DuffelApiException(
                "Malformed response from Duffel", e);
        }

        try {
            return duffelResponseMapper.mapToDomain(responseWrapper);
        } catch (RuntimeException e) {
            throw new com.apiintegration.flightsearch.infrastructure.provider.duffel.exception.DuffelApiException(
                    "Malformed response from Duffel", e);
        }
    }

    private List<DuffelOfferRequestPayload.Passenger> toDuffelPassengers(FlightSearchRequest request) {
        if (request.passengers() == null || request.passengers().isEmpty()) {
            return List.of(DuffelOfferRequestPayload.Passenger.builder().type("adult").build());
        }

        return request.passengers().stream()
                .map(passenger -> DuffelOfferRequestPayload.Passenger.builder()
                        .type(passenger.type().name().toLowerCase())
                        .build())
                .collect(Collectors.toList());
    }
}
