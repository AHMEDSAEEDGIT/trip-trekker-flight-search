package com.apiintegration.flightsearch.application.port;

import com.apiintegration.flightsearch.api.dto.request.FlightSearchRequest;
import com.apiintegration.flightsearch.domain.model.FlightOffer;

import java.util.List;

public interface FlightSearchProvider {
    List<FlightOffer> searchFlights(FlightSearchRequest request);
}
