package com.apiintegration.flightsearch.infrastructure.provider.duffel;

import com.apiintegration.flightsearch.infrastructure.provider.duffel.dto.DuffelOfferRequestPayload;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class DuffelClient {
    private final WebClient duffelWebClient;
    private final DuffelAuthService authService;

    public DuffelClient(WebClient duffelWebClient, DuffelAuthService authService) {
        this.duffelWebClient = duffelWebClient;
        this.authService = authService;
    }
    public String getAircraft(String aircraftId) {
        return duffelWebClient.get()
                .uri("/air/aircraft/{id}", aircraftId)
                .headers(headers -> headers.setBearerAuth(authService.getAccessToken()))
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.UNAUTHORIZED.value(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new DuffelAuthenticationException()))
                .bodyToMono(String.class)
                .block();
    }

    public String createOfferRequest(DuffelOfferRequestPayload payload) {
        return duffelWebClient.post()
                .uri("/air/offer_requests")
                .headers(headers -> headers.setBearerAuth(authService.getAccessToken()))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}