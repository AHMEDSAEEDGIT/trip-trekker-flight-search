package com.apiintegration.flightsearch.infrastructure.provider.duffel;

import com.apiintegration.flightsearch.domain.model.Airline;
import com.apiintegration.flightsearch.domain.model.FlightOffer;
import com.apiintegration.flightsearch.domain.model.FlightSegment;
import com.apiintegration.flightsearch.domain.model.Price;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.dto.DuffelResponseWrapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DuffelResponseMapper {

    public List<FlightOffer> mapToDomain(DuffelResponseWrapper response) {
        if (response == null || response.getData() == null || response.getData().getOffers() == null) {
            return List.of();
        }

        return response.getData().getOffers().stream().map(duffelOffer -> {
            FlightOffer offer = new FlightOffer();
            offer.setId(duffelOffer.getId());

            // Map Price
            Price price = new Price();
            price.setAmount(duffelOffer.getTotalAmount());
            price.setCurrency(duffelOffer.getTotalCurrency());
            offer.setPrice(price);

            // Map Airline
            if (duffelOffer.getOwner() != null) {
                Airline airline = new Airline();
                airline.setCode(duffelOffer.getOwner().getIataCode());
                airline.setName(duffelOffer.getOwner().getName());
                offer.setAirline(airline);
            }

            // Map Segments (flattening slices into segments)
            List<FlightSegment> segments = duffelOffer.getSlices() == null ? List.of() : duffelOffer.getSlices().stream()
                    .filter(slice -> slice != null && slice.getSegments() != null)
                    .flatMap(slice -> slice.getSegments().stream())
                    .filter(ds -> ds != null)
                    .map(ds -> {
                        FlightSegment seg = new FlightSegment();
                        seg.setOrigin(locationCode(ds.getOrigin()));
                        seg.setDestination(locationCode(ds.getDestination()));
                        seg.setDepartureTime(parseDateTime(ds.getDepartingAt()));
                        seg.setArrivalTime(parseDateTime(ds.getArrivingAt()));
                        seg.setFlightNumber(ds.getFlightNumber());
                        return seg;
                    }).collect(Collectors.toList());

            offer.setSegments(segments);
            return offer;
        }).collect(Collectors.toList());
    }

    private String locationCode(DuffelResponseWrapper.DuffelLocation location) {
        return location == null ? null : location.getIataCode();
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return OffsetDateTime.parse(value).toLocalDateTime();
        } catch (DateTimeParseException ex) {
            return LocalDateTime.parse(value);
        }
    }
}
