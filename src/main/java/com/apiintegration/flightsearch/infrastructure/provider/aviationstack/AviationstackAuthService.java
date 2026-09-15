package com.apiintegration.flightsearch.infrastructure.provider.aviationstack;

import com.apiintegration.flightsearch.config.AviationstackProperties;
import org.springframework.stereotype.Service;

@Service
public class AviationstackAuthService {
    private final AviationstackProperties properties;

    public AviationstackAuthService(AviationstackProperties properties) {
        this.properties = properties;
    }

    public String getApiKey() {
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new IllegalStateException("AVIATIONSTACK_KEY is not configured in environment variables");
        }
        return properties.getApiKey();
    }
}
