package com.apiintegration.flightsearch.api.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlightSearchRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void normalizesAirportCodesAndAcceptsAdultSearch() {
        FlightSearchRequest request = new FlightSearchRequest(
                " jfk ", "lhr", LocalDate.of(2026, 9, 15), null,
                List.of(new Passenger(PassengerType.ADULT)), Cabin.ECONOMY);

        assertThat(request.origin()).isEqualTo("JFK");
        assertThat(request.destination()).isEqualTo("LHR");
        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsReturnDateBeforeDepartureAndSearchWithoutAdult() {
        FlightSearchRequest request = new FlightSearchRequest(
                "JFK", "LHR", LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 14),
                List.of(new Passenger(PassengerType.CHILD)), Cabin.ECONOMY);

        assertThat(validator.validate(request)).hasSize(2);
    }
}
