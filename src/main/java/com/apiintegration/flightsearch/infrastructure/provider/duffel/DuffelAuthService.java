package com.apiintegration.flightsearch.infrastructure.provider.duffel;

import com.apiintegration.flightsearch.config.DuffelProperties;
import org.springframework.stereotype.Service;

@Service
public class DuffelAuthService {
    private final DuffelProperties properties;

    public DuffelAuthService(DuffelProperties properties) {
        this.properties = properties;
    }

    public String getAccessToken() {
        String token = properties.getToken();
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("DUFFEL_ACCESS_TOKEN is not configured");
        }
        return token;
    }
}