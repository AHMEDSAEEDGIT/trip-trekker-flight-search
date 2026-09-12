package com.apiintegration.flightsearch.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record Passenger(@NotNull PassengerType type) {
}