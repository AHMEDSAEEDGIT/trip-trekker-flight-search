package com.apiintegration.flightsearch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient duffelWebClient(AppProperties properties){
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(properties.getTimeout()));

        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Duffel-Version", "v2")
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .codecs(configurer -> configurer.defaultCodecs()
                    .maxInMemorySize(properties.getMaxResponseSize()))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public WebClient flightApiWebClient(FlightApiProperties properties){
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(properties.getTimeout()));

        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize((int) properties.getMaxResponseSize()))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
