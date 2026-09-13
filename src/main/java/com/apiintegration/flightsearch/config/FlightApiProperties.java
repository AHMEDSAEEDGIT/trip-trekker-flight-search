package com.apiintegration.flightsearch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "flight.providers.flightapi")
@Getter
@Setter
public class FlightApiProperties {

    private String baseUrl;
    private String apiKey;
    private int timeout;
    private int maxResponseSize;

}
