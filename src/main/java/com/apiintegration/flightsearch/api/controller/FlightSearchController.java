package com.apiintegration.flightsearch.api.controller;

import com.apiintegration.flightsearch.api.dto.request.FlightSearchRequest;
import com.apiintegration.flightsearch.application.service.FlightSearchService;
import com.apiintegration.flightsearch.domain.model.FlightOffer;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class FlightSearchController {

    private final FlightSearchService flightSearchService;

    @PostMapping("/search")
    public ResponseEntity<List<FlightOffer>> searchFlights(@Valid @RequestBody FlightSearchRequest request) {
        List<FlightOffer> offers = flightSearchService.searchFlights(request);
        return ResponseEntity.ok(offers);
    }
}
