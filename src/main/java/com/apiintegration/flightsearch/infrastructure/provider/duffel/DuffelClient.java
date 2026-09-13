package com.apiintegration.flightsearch.infrastructure.provider.duffel;

import com.apiintegration.flightsearch.config.AppProperties;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.dto.DuffelOfferRequestPayload;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.exception.DuffelApiException;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.exception.DuffelAuthenticationException;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.exception.DuffelTimeoutException;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.exception.DuffelUnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class DuffelClient {
    private final WebClient duffelWebClient;
    private final DuffelAuthService authService;
    private final AppProperties properties;

    public DuffelClient(WebClient duffelWebClient, DuffelAuthService authService, AppProperties properties) {
        this.duffelWebClient = duffelWebClient;
        this.authService = authService;
        this.properties = properties;
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
                .onStatus(status -> status.value() == 401,
                        response -> Mono.error(new DuffelUnauthorizedException("Invalid or expired Duffel token")))
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new DuffelApiException("Client error from Duffel: " + body))))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new DuffelApiException("Duffel server is down or experiencing issues")))
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(properties.getTimeout()))
                .onErrorResume(java.util.concurrent.TimeoutException.class, ex -> {
                    throw new DuffelTimeoutException("Duffel API request timed out");
                })
                .onErrorResume(org.springframework.web.reactive.function.client.WebClientRequestException.class, ex -> {
                    throw new DuffelApiException("Failed to connect to Duffel API (API down or network error)");
                })
                .block();
    }
}