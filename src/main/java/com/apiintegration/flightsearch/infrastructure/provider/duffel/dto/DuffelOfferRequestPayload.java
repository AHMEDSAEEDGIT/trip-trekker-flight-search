package com.apiintegration.flightsearch.infrastructure.provider.duffel.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DuffelOfferRequestPayload {
    private DataWrapper data;

    @Data
    @Builder
    public static class DataWrapper {
        private List<Slice> slices;
        private List<Passenger> passengers;
        private String cabinClass;
    }

    @Data
    @Builder
    public static class Slice {
        private String origin;
        private String destination;
        private String departureDate; // Format: "YYYY-MM-DD"
    }

    @Data
    @Builder
    public static class Passenger {
        private String type; // e.g., "adult"
    }
}
