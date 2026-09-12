package com.apiintegration.flightsearch.infrastructure.provider.duffel;

import com.apiintegration.flightsearch.domain.model.Airline;
import com.apiintegration.flightsearch.domain.model.FlightOffer;
import com.apiintegration.flightsearch.domain.model.FlightSegment;
import com.apiintegration.flightsearch.domain.model.Price;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.dto.DuffelResponseWrapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
            List<FlightSegment> segments = duffelOffer.getSlices().stream()
                    .flatMap(slice -> slice.getSegments().stream())
                    .map(ds -> {
                        FlightSegment seg = new FlightSegment();
                        seg.setOrigin(ds.getOrigin());
                        seg.setDestination(ds.getDestination());
                        seg.setDepartureTime(LocalDateTime.parse(ds.getDepartingAt()));
                        seg.setArrivalTime(LocalDateTime.parse(ds.getArrivingAt()));
                        seg.setFlightNumber(ds.getFlightNumber());
//                        seg.setFlightOffer(offer);
                        return seg;
                    }).collect(Collectors.toList());

            offer.setSegments(segments);
            return offer;
        }).collect(Collectors.toList());
    }
}
