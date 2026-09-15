package com.apiintegration.flightsearch.api.controller;

import com.apiintegration.flightsearch.api.dto.request.FlightSearchRequest;
import com.apiintegration.flightsearch.application.port.FlightSearchProvider;
import com.apiintegration.flightsearch.domain.model.FlightOffer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightSearchController {

    private final FlightSearchProvider flightSearchProvider;

    public FlightSearchController(@Qualifier("aviationstackService") FlightSearchProvider flightSearchProvider) {
        this.flightSearchProvider = flightSearchProvider;
    }

    @PostMapping("/search")
    public ResponseEntity<List<FlightOffer>> searchFlights(@Valid @RequestBody FlightSearchRequest request) {
        List<FlightOffer> offers = flightSearchProvider.searchFlights(request);
        return ResponseEntity.ok(offers);
    }
}
