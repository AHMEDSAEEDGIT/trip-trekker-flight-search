package com.apiintegration.flightsearch.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


import java.time.LocalDate;
import java.util.List;

public record FlightSearchRequest(
        @NotBlank String origin,
        @NotBlank String destination,
        @NotNull @FutureOrPresent LocalDate departureDate,
        @FutureOrPresent LocalDate returnDate,
        @Valid List<Passenger> passengers,
        @NotNull Cabin cabinClass
        ) {
    public FlightSearchRequest {
        passengers = passengers != null ? List.copyOf(passengers) : List.of();
    }
}

