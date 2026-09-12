package com.apiintegration.flightsearch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "flight.providers.duffel")
@Getter
@Setter
public class AppProperties {
    private String baseUrl;
    private String token;
    private int timeout;
}
