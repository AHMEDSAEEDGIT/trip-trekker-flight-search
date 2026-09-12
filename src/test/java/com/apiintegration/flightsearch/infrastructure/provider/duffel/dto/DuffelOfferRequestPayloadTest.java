package com.apiintegration.flightsearch.infrastructure.provider.duffel.dto;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DuffelOfferRequestPayloadTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesDuffelFieldNames() throws Exception {
        DuffelOfferRequestPayload payload = DuffelOfferRequestPayload.builder()
                .data(DuffelOfferRequestPayload.DataWrapper.builder()
                        .slices(List.of(DuffelOfferRequestPayload.Slice.builder()
                                .origin("JFK")
                                .destination("LHR")
                                .departureDate("2026-09-15")
                                .build()))
                        .cabinClass("economy")
                        .passengers(List.of(DuffelOfferRequestPayload.Passenger.builder()
                                .type("adult")
                                .build()))
                        .build())
                .build();

        String json = objectMapper.writeValueAsString(payload);

        assertThat(json).contains("\"departure_date\":\"2026-09-15\"");
        assertThat(json).contains("\"cabin_class\":\"economy\"");
        assertThat(json).doesNotContain("departureDate");
    }
}