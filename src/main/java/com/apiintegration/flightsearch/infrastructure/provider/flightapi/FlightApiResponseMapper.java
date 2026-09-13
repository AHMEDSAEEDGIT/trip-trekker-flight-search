package com.apiintegration.flightsearch.infrastructure.provider.flightapi;

import com.apiintegration.flightsearch.domain.model.Airline;
import com.apiintegration.flightsearch.domain.model.FlightOffer;
import com.apiintegration.flightsearch.domain.model.FlightSegment;
import com.apiintegration.flightsearch.domain.model.Price;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.dto.FlightApiResponseWrapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FlightApiResponseMapper {
    public List<FlightOffer> mapToDomain(FlightApiResponseWrapper response, String currencyCode) {
        if (response == null || response.getItineraries() == null) {
            return List.of();
        }

        // Index legs, segments, and airlines for quick lookup by ID
        Map<String, FlightApiResponseWrapper.Leg> legMap = response.getLegs() != null
                ? response.getLegs().stream().collect(Collectors.toMap(FlightApiResponseWrapper.Leg::getId, Function.identity()))
                : Map.of();

        Map<String, FlightApiResponseWrapper.Segment> segmentMap = response.getSegments() != null
                ? response.getSegments().stream().collect(Collectors.toMap(FlightApiResponseWrapper.Segment::getId, Function.identity()))
                : Map.of();

        Map<Integer, FlightApiResponseWrapper.Carrier> carrierMap = response.getCarriers() != null
            ? response.getCarriers().stream().collect(Collectors.toMap(FlightApiResponseWrapper.Carrier::getId, Function.identity()))
                : Map.of();

        Map<Integer, FlightApiResponseWrapper.Place> placeMap = response.getPlaces() != null
            ? response.getPlaces().stream().collect(Collectors.toMap(FlightApiResponseWrapper.Place::getId, Function.identity()))
            : Map.of();

        return response.getItineraries().stream().map(itinerary -> {
            FlightOffer offer = new FlightOffer();
            offer.setId(itinerary.getId());

            // Pick the first pricing option available
            if (itinerary.getPricingOptions() != null && !itinerary.getPricingOptions().isEmpty()) {
                var pricing = itinerary.getPricingOptions().get(0);
                Price price = new Price();
                price.setAmount(pricing.getPrice() != null ? pricing.getPrice().getAmount() : null);
                price.setCurrency(currencyCode);
                offer.setPrice(price);
            }

            // Map legs -> segments
                List<FlightSegment> flightSegments = (itinerary.getLegIds() == null ? List.<String>of() : itinerary.getLegIds()).stream()
                    .map(legMap::get)
                    .filter(java.util.Objects::nonNull)
                    .flatMap(leg -> {
                        // Find primary airline for the leg if present
                        Airline airline = null;
                        if (leg.getMarketingCarrierIds() != null && !leg.getMarketingCarrierIds().isEmpty()) {
                            var carrier = carrierMap.get(leg.getMarketingCarrierIds().get(0));
                            if (carrier != null) {
                                airline = new Airline();
                                airline.setCode(carrier.getCode());
                                airline.setName(carrier.getName());
                                offer.setAirline(airline);
                            }
                        }

                        return (leg.getSegmentIds() == null ? List.<String>of() : leg.getSegmentIds()).stream()
                                .map(segmentMap::get)
                                .filter(java.util.Objects::nonNull)
                                .map(segDto -> {
                                    FlightSegment seg = new FlightSegment();
                                    var origin = placeMap.get(segDto.getOriginPlaceId());
                                    var destination = placeMap.get(segDto.getDestinationPlaceId());
                                    seg.setOrigin(origin != null ? origin.getCode() : null);
                                    seg.setDestination(destination != null ? destination.getCode() : null);
                                    seg.setDepartureTime(parseDateTime(segDto.getDeparture(), "departure"));
                                    seg.setArrivalTime(parseDateTime(segDto.getArrival(), "arrival"));
                                    seg.setFlightNumber(segDto.getMarketingFlightNumber());
//                                    seg.setFlightOffer(offer);
                                    return seg;
                                });
                    }).collect(Collectors.toList());

            offer.setSegments(flightSegments);
            return offer;
        }).collect(Collectors.toList());
    }

    private LocalDateTime parseDateTime(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return OffsetDateTime.parse(value).toLocalDateTime();
        } catch (DateTimeParseException ex) {
            try {
                return LocalDateTime.parse(value);
            } catch (DateTimeParseException localDateTimeException) {
                throw new IllegalArgumentException("Invalid FlightAPI " + fieldName + " value: " + value, localDateTimeException);
            }
        }
    }
}
