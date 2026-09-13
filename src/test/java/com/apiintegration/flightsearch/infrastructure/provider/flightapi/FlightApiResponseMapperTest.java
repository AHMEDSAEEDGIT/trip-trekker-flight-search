package com.apiintegration.flightsearch.infrastructure.provider.flightapi;

import com.apiintegration.flightsearch.domain.model.FlightOffer;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.dto.FlightApiResponseWrapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlightApiResponseMapperTest {
    private final FlightApiResponseMapper mapper = new FlightApiResponseMapper();

    @Test
    void mapsSegmentWithMissingTimesWithoutThrowing() {
        FlightApiResponseWrapper.Segment segment = new FlightApiResponseWrapper.Segment();
        segment.setId("segment-1");
        segment.setOriginPlaceId(1);
        segment.setDestinationPlaceId(2);
        segment.setDeparture("2026-09-15T08:30:00Z");
        segment.setArrival("2026-09-15T20:45:00");
        segment.setMarketingFlightNumber("BA112");

        FlightApiResponseWrapper.Place origin = new FlightApiResponseWrapper.Place();
        origin.setId(1);
        origin.setCode("JFK");

        FlightApiResponseWrapper.Place destination = new FlightApiResponseWrapper.Place();
        destination.setId(2);
        destination.setCode("LHR");

        FlightApiResponseWrapper.Leg leg = new FlightApiResponseWrapper.Leg();
        leg.setId("leg-1");
        leg.setSegmentIds(List.of("segment-1"));

        FlightApiResponseWrapper.Itinerary itinerary = new FlightApiResponseWrapper.Itinerary();
        itinerary.setId("itinerary-1");
        itinerary.setLegIds(List.of("leg-1"));

        FlightApiResponseWrapper response = new FlightApiResponseWrapper();
        response.setItineraries(List.of(itinerary));
        response.setLegs(List.of(leg));
        response.setSegments(List.of(segment));
        response.setPlaces(List.of(origin, destination));

        List<FlightOffer> offers = mapper.mapToDomain(response, "USD");

        assertThat(offers).singleElement().satisfies(offer ->
                assertThat(offer.getSegments()).singleElement().satisfies(mappedSegment -> {
                    assertThat(mappedSegment.getOrigin()).isEqualTo("JFK");
                    assertThat(mappedSegment.getDestination()).isEqualTo("LHR");
                    assertThat(mappedSegment.getDepartureTime()).isEqualTo(LocalDateTime.of(2026, 9, 15, 8, 30));
                    assertThat(mappedSegment.getArrivalTime()).isEqualTo(LocalDateTime.of(2026, 9, 15, 20, 45));
                    assertThat(mappedSegment.getFlightNumber()).isEqualTo("BA112");
                }));
    }
}
