package com.apiintegration.flightsearch.infrastructure.provider.aviationstack;

import com.apiintegration.flightsearch.config.AviationstackProperties;
import com.apiintegration.flightsearch.infrastructure.provider.aviationstack.dto.AviationstackFlightSearchRequest;
import com.apiintegration.flightsearch.infrastructure.provider.aviationstack.exception.AviationstackApiException;
import com.apiintegration.flightsearch.infrastructure.provider.aviationstack.exception.AviationstackDownException;
import com.apiintegration.flightsearch.infrastructure.provider.aviationstack.exception.AviationstackTimeoutException;
import com.apiintegration.flightsearch.infrastructure.provider.aviationstack.exception.AviationstackUnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
public class AviationstackClient {
    private final WebClient aviationstackWebClient;
    private final AviationstackAuthService authService;
    private final AviationstackProperties properties;

    public AviationstackClient(WebClient aviationstackWebClient,
                               AviationstackAuthService authService,
                               AviationstackProperties properties) {
        this.aviationstackWebClient = aviationstackWebClient;
        this.authService = authService;
        this.properties = properties;
    }

    public String searchFlights(AviationstackFlightSearchRequest request) {
        return aviationstackWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/flights")
                        .queryParam("access_key", authService.getApiKey())
                        .queryParam("dep_iata", request.origin())
                        .queryParam("arr_iata", request.destination())
                        .queryParam("flight_date", request.flightDate())
                        .queryParam("flight_status", request.flightStatus())
                        .queryParam("limit", request.limit())
                        .build())
                .retrieve()
                .onStatus(status -> status.value() == 401 || status.value() == 403,
                        response -> Mono.error(new AviationstackUnauthorizedException("Invalid Aviationstack credentials")))
                .onStatus(status -> status.is4xxClientError(), response -> response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new AviationstackApiException("Client error from Aviationstack: " + body))))
                .onStatus(status -> status.is5xxServerError(), response ->
                        Mono.error(new AviationstackDownException("Aviationstack server is unavailable", null)))
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(properties.getTimeout()))
                .onErrorMap(TimeoutException.class, ex -> new AviationstackTimeoutException(
                        "Aviationstack request timed out after " + properties.getTimeout() + " ms"))
                .onErrorMap(WebClientRequestException.class, ex -> new AviationstackDownException(
                        "Failed to connect to Aviationstack", ex))
                .block();
    }
}