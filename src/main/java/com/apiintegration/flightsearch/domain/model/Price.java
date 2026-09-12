package com.apiintegration.flightsearch.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Price {
    private BigDecimal amount;
    private String currency;

}
