package com.apiintegration.flightsearch.application.service;

import com.apiintegration.flightsearch.api.dto.request.FlightSearchRequest;
import com.apiintegration.flightsearch.application.port.FlightSearchProvider;
import com.apiintegration.flightsearch.domain.model.FlightOffer;
import com.apiintegration.flightsearch.infrastructure.provider.aviationstack.AviationstackClient;
import com.apiintegration.flightsearch.infrastructure.provider.aviationstack.AviationstackResponseMapper;
import com.apiintegration.flightsearch.infrastructure.provider.aviationstack.dto.AviationstackFlightSearchRequest;
import com.apiintegration.flightsearch.infrastructure.provider.aviationstack.dto.AviationstackResponseWrapper;
import com.apiintegration.flightsearch.infrastructure.provider.aviationstack.exception.AviationstackMalformedResponseException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class AviationstackService implements FlightSearchProvider {
    private final AviationstackClient aviationstackClient;
    private final AviationstackResponseMapper responseMapper;
    private final ObjectMapper objectMapper;

    public AviationstackService(AviationstackClient aviationstackClient,
                                AviationstackResponseMapper responseMapper,
                                ObjectMapper objectMapper) {
        this.aviationstackClient = aviationstackClient;
        this.responseMapper = responseMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<FlightOffer> searchFlights(FlightSearchRequest request) {
        AviationstackFlightSearchRequest providerRequest = new AviationstackFlightSearchRequest(
                request.origin(),
                request.destination(),
                request.departureDate().toString(),
                "scheduled",
                100);

        String rawJson = aviationstackClient.searchFlights(providerRequest);
        try {
            AviationstackResponseWrapper response = objectMapper.readValue(rawJson, AviationstackResponseWrapper.class);
            return responseMapper.mapToDomain(response);
        } catch (Exception exception) {
            throw new AviationstackMalformedResponseException(
                    "Invalid or malformed response from Aviationstack", exception);
        }
    }
}