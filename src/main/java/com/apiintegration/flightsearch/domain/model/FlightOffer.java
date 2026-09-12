package com.apiintegration.flightsearch.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlightOffer {
    private String id;
    private Price price;
    private Airline airline;
    private List<FlightSegment> segments;
}
