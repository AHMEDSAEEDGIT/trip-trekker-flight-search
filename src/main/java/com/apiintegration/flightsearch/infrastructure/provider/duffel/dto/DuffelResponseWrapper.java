package com.apiintegration.flightsearch.infrastructure.provider.duffel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class DuffelResponseWrapper {
    private DuffelData data;

    @Getter
    @Setter
    public static class DuffelData {
        private List<DuffelOffer> offers;
    }

    @Getter
    @Setter
    public static class DuffelOffer {
        private String id;

        @JsonProperty("total_amount")
        private BigDecimal totalAmount;

        @JsonProperty("total_currency")
        private String totalCurrency;
        private DuffelOwner owner;
        private List<DuffelSlice> slices;
    }

    @Getter
    @Setter
    public static class DuffelOwner {
        @JsonProperty("iata_code")
        private String iataCode;
        private String name;
    }

    @Getter
    @Setter
    public static class DuffelSlice {
        private List<DuffelSegment> segments;
    }

    @Getter
    @Setter
    public static class DuffelSegment {
        private DuffelLocation origin;
        private DuffelLocation destination;
        @JsonProperty("departing_at")
        private String departingAt;
        @JsonProperty("arriving_at")
        private String arrivingAt;
        @JsonProperty("flight_number")
        private String flightNumber;
    }

    @Getter
    @Setter
    public static class DuffelLocation {
        @JsonProperty("iata_code")
        private String iataCode;
        private String name;
    }
}
