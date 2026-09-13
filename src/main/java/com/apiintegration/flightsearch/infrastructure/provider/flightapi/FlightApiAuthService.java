package com.apiintegration.flightsearch.infrastructure.provider.flightapi;

import com.apiintegration.flightsearch.config.FlightApiProperties;
import org.springframework.stereotype.Service;

@Service
public class FlightApiAuthService {

    private final FlightApiProperties properties;

    public FlightApiAuthService(FlightApiProperties properties) {
        this.properties = properties;
    }

    public String getAccessToken() {
        String token = properties.getApiKey();
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("FLIGHTAPI_KEY is not configured in environment variables");
        }
        return token;
    }

}
