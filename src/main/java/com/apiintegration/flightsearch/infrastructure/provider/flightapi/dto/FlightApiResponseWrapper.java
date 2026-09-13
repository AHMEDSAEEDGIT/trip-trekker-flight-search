package com.apiintegration.flightsearch.infrastructure.provider.flightapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FlightApiResponseWrapper {

    private List<Itinerary> itineraries;
    private List<Leg> legs;
    private List<Segment> segments;
    private List<Place> places;
    private List<Carrier> carriers;

    @Data
    public static class Itinerary {
        private String id;
        @JsonProperty("leg_ids")
        private List<String> legIds;
        @JsonProperty("pricing_options")
        private List<PricingOption> pricingOptions;
    }

    @Data
    public static class PricingOption {
        private String id;
        private PriceInfo price;
    }

    @Data
    public static class PriceInfo {
        private BigDecimal amount;
    }

    @Data
    public static class Leg {
        private String id;
        @JsonProperty("origin_place_id")
        private Integer originPlaceId;
        @JsonProperty("destination_place_id")
        private Integer destinationPlaceId;
        private String departure;
        private String arrival;
        @JsonProperty("segment_ids")
        private List<String> segmentIds;
        @JsonProperty("marketing_carrier_ids")
        private List<Integer> marketingCarrierIds;
    }

    @Data
    public static class Segment {
        private String id;
        @JsonProperty("origin_place_id")
        private Integer originPlaceId;
        @JsonProperty("destination_place_id")
        private Integer destinationPlaceId;
        private String departure;
        private String arrival;
        @JsonProperty("marketing_flight_number")
        private String marketingFlightNumber;
        @JsonProperty("marketing_carrier_id")
        private Integer marketingCarrierId;
    }

    @Data
    public static class Place {
        private Integer id;
        @JsonAlias({"code", "iata_code", "display_code"})
        private String code;
        private String name;
    }

    @Data
    public static class Carrier {
        private Integer id;
        @JsonAlias({"code", "iata_code", "display_code"})
        private String code;
        private String name;
    }
}
