package com.apiintegration.flightsearch.infrastructure.provider.aviationstack;

import com.apiintegration.flightsearch.domain.model.Airline;
import com.apiintegration.flightsearch.domain.model.FlightOffer;
import com.apiintegration.flightsearch.domain.model.FlightSegment;
import com.apiintegration.flightsearch.infrastructure.provider.aviationstack.dto.AviationstackResponseWrapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Function;

@Component
public class AviationstackResponseMapper {
    public List<FlightOffer> mapToDomain(AviationstackResponseWrapper response) {
        if (response == null || response.getData() == null) {
            return List.of();
        }

        return response.getData().stream().filter(java.util.Objects::nonNull).map(flight -> {
            FlightOffer offer = new FlightOffer();
            offer.setId(firstNonBlank(value(flight.getFlight(), AviationstackResponseWrapper.FlightNumber::getIata),
                    value(flight.getFlight(), AviationstackResponseWrapper.FlightNumber::getIcao), flight.getFlightDate()));

            if (flight.getAirline() != null) {
                Airline airline = new Airline();
                airline.setCode(firstNonBlank(flight.getAirline().getIata(), flight.getAirline().getIcao()));
                airline.setName(flight.getAirline().getName());
                offer.setAirline(airline);
            }

            FlightSegment segment = new FlightSegment();
            if (flight.getDeparture() != null) {
                segment.setOrigin(firstNonBlank(flight.getDeparture().getIata(), flight.getDeparture().getIcao()));
                segment.setDepartureTime(parseDateTime(flight.getDeparture().getScheduled()));
            }
            if (flight.getArrival() != null) {
                segment.setDestination(firstNonBlank(flight.getArrival().getIata(), flight.getArrival().getIcao()));
                segment.setArrivalTime(parseDateTime(flight.getArrival().getScheduled()));
            }
            segment.setFlightNumber(firstNonBlank(value(flight.getFlight(), AviationstackResponseWrapper.FlightNumber::getIata),
                    value(flight.getFlight(), AviationstackResponseWrapper.FlightNumber::getNumber)));
            offer.setSegments(List.of(segment));
            return offer;
        }).toList();
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDateTime.parse(value);
            } catch (DateTimeParseException exception) {
                throw new IllegalArgumentException("Invalid Aviationstack date-time value: " + value, exception);
            }
        }
    }

    private String value(AviationstackResponseWrapper.FlightNumber flight,
                         Function<AviationstackResponseWrapper.FlightNumber, String> getter) {
        return flight == null ? null : getter.apply(flight);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}