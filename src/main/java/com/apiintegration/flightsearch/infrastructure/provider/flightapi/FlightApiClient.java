package com.apiintegration.flightsearch.infrastructure.provider.flightapi;

import com.apiintegration.flightsearch.config.FlightApiProperties;
import com.apiintegration.flightsearch.infrastructure.provider.duffel.DuffelAuthService;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception.FlightApiDownException;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception.FlightApiException;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception.FlightApiTimeoutException;
import com.apiintegration.flightsearch.infrastructure.provider.flightapi.exception.FlightApiUnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class FlightApiClient {
    private final WebClient flightApiWebClient;
    private final FlightApiAuthService authService;
        private final FlightApiProperties properties;


    public String searchOneWayTrip(String origin, String destination, String date,
                                   int adults, int children, int infants,
                                   String cabinClass, String currency) {
        String apiKey = authService.getAccessToken();

        return flightApiWebClient.get()
                .uri("/onewaytrip/{apiKey}/{origin}/{destination}/{date}/{adults}/{children}/{infants}/{cabinClass}/{currency}",
                        apiKey, origin, destination, date, adults, children, infants, cabinClass, currency)
                .retrieve()
                .onStatus(status -> status.value() == 401 || status.value() == 403,
                        response -> Mono.error(new FlightApiUnauthorizedException("Invalid FlightAPI credentials")))
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new FlightApiException("Client error from FlightAPI: " + body))))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new FlightApiDownException("FlightAPI server is down or returning a 5xx error", null)))
                .bodyToMono(String.class)
                                .timeout(Duration.ofMillis(properties.getTimeout()))
                .onErrorResume(TimeoutException.class, ex -> {
                                        throw new FlightApiTimeoutException("FlightAPI request timed out after " + properties.getTimeout() + " ms");
                })
                .onErrorResume(WebClientRequestException.class, ex -> {
                    throw new FlightApiDownException("Failed to connect to FlightAPI (API down or network error)", ex);
                })
                .block();
    }
}
