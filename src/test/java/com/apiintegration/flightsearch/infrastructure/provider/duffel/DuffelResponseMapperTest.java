package com.apiintegration.flightsearch.infrastructure.provider.duffel;

import com.apiintegration.flightsearch.domain.model.FlightOffer;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.dto.DuffelResponseWrapper;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DuffelResponseMapperTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DuffelResponseMapper mapper = new DuffelResponseMapper();

    @Test
    void mapsNestedLocationsToIataCodes() throws Exception {
        String json = """
                {
                  "data": {
                    "offers": [{
                      "id": "offer-1",
                      "total_amount": "420.50",
                      "total_currency": "USD",
                      "slices": [{
                        "segments": [{
                          "origin": {"iata_code": "JFK", "name": "John F Kennedy"},
                          "destination": {"iata_code": "LHR", "name": "Heathrow"},
                          "departing_at": "2026-09-15T08:30:00",
                          "arriving_at": "2026-09-15T20:45:00",
                          "flight_number": "BA112"
                        }]
                      }]
                    }]
                  }
                }
                """;

        DuffelResponseWrapper response = objectMapper.readValue(json, DuffelResponseWrapper.class);
        List<FlightOffer> offers = mapper.mapToDomain(response);

        assertThat(offers).hasSize(1);
        assertThat(offers.get(0).getSegments()).singleElement()
                .satisfies(segment -> {
                    assertThat(segment.getOrigin()).isEqualTo("JFK");
                    assertThat(segment.getDestination()).isEqualTo("LHR");
                });
    }
}