package com.apiintegration.flightsearch.infrastructure.provider.aviationstack.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AviationstackResponseWrapper {
    private Pagination pagination;
    private List<Flight> data;

    @Data
    public static class Pagination {
        private Integer limit;
        private Integer offset;
        private Integer count;
        private Integer total;
    }

    @Data
    public static class Flight {
        @JsonProperty("flight_date")
        private String flightDate;
        @JsonProperty("flight_status")
        private String flightStatus;
        private Airport departure;
        private Airport arrival;
        private Airline airline;
        private FlightNumber flight;
    }

    @Data
    public static class Airport {
        private String airport;
        private String iata;
        private String icao;
        private String terminal;
        private String gate;
        private String scheduled;
        private String estimated;
        private String actual;
    }

    @Data
    public static class Airline {
        private String name;
        private String iata;
        private String icao;
    }

    @Data
    public static class FlightNumber {
        private String number;
        private String iata;
        private String icao;
    }
}