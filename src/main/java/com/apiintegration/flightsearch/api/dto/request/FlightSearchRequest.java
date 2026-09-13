package com.apiintegration.flightsearch.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;


import java.time.LocalDate;
import java.util.List;

public record FlightSearchRequest(
    @NotBlank @Size(min = 3, max = 3) String origin,
    @NotBlank @Size(min = 3, max = 3) String destination,
        @NotNull @FutureOrPresent LocalDate departureDate,
        @FutureOrPresent LocalDate returnDate,
        List<@Valid Passenger> passengers,
        @NotNull Cabin cabinClass
        ) {
    public FlightSearchRequest {
        origin = origin == null ? null : origin.trim().toUpperCase();
        destination = destination == null ? null : destination.trim().toUpperCase();
        passengers = passengers != null ? List.copyOf(passengers) : List.of();
    }

    @AssertTrue(message = "returnDate must be on or after departureDate")
    public boolean isReturnDateValid() {
        return departureDate == null || returnDate == null || !returnDate.isBefore(departureDate);
    }

    @AssertTrue(message = "at least one adult passenger is required")
    public boolean hasAdultPassenger() {
        return passengers.isEmpty() || passengers.stream().anyMatch(passenger ->
                passenger != null && passenger.type() == PassengerType.ADULT);
    }
}

