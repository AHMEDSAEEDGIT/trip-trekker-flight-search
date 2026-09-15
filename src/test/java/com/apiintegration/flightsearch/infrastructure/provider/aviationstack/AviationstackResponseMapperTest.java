package com.apiintegration.flightsearch.infrastructure.provider.aviationstack;

import com.apiintegration.flightsearch.domain.model.FlightOffer;
import com.apiintegration.flightsearch.infrastructure.provider.aviationstack.dto.AviationstackResponseWrapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AviationstackResponseMapperTest {
    private final AviationstackResponseMapper mapper = new AviationstackResponseMapper();

    @Test
    void mapsAviationstackFlightIntoDomainOffer() {
        AviationstackResponseWrapper.Flight flight = new AviationstackResponseWrapper.Flight();
        flight.setFlightDate("2026-09-15");

        AviationstackResponseWrapper.Airline airline = new AviationstackResponseWrapper.Airline();
        airline.setIata("BA");
        airline.setName("British Airways");
        flight.setAirline(airline);

        AviationstackResponseWrapper.FlightNumber number = new AviationstackResponseWrapper.FlightNumber();
        number.setIata("BA112");
        flight.setFlight(number);

        AviationstackResponseWrapper.Airport departure = new AviationstackResponseWrapper.Airport();
        departure.setIata("JFK");
        departure.setScheduled("2026-09-15T08:30:00+00:00");
        flight.setDeparture(departure);

        AviationstackResponseWrapper.Airport arrival = new AviationstackResponseWrapper.Airport();
        arrival.setIata("LHR");
        arrival.setScheduled("2026-09-15T20:45:00+00:00");
        flight.setArrival(arrival);

        AviationstackResponseWrapper response = new AviationstackResponseWrapper();
        response.setData(List.of(flight));

        List<FlightOffer> offers = mapper.mapToDomain(response);

        assertThat(offers).singleElement().satisfies(offer -> {
            assertThat(offer.getId()).isEqualTo("BA112");
            assertThat(offer.getPrice()).isNull();
            assertThat(offer.getAirline().getCode()).isEqualTo("BA");
            assertThat(offer.getSegments()).singleElement().satisfies(segment -> {
                assertThat(segment.getOrigin()).isEqualTo("JFK");
                assertThat(segment.getDestination()).isEqualTo("LHR");
                assertThat(segment.getFlightNumber()).isEqualTo("BA112");
                assertThat(segment.getDepartureTime()).isEqualTo(LocalDateTime.of(2026, 9, 15, 8, 30));
                assertThat(segment.getArrivalTime()).isEqualTo(LocalDateTime.of(2026, 9, 15, 20, 45));
            });
        });
    }
}