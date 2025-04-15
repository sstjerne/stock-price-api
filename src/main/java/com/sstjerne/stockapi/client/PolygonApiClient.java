package com.sstjerne.stockapi.client;

import com.sstjerne.stockapi.dto.PolygonApiResponse;
import com.sstjerne.stockapi.exception.PolygonApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class PolygonApiClient {

    private final WebClient webClient;

    @Value("${polygon.api.api-key}")
    private String apiKey;

    public Mono<PolygonApiResponse> getStockData(String symbol, LocalDate fromDate, LocalDate toDate) {
        String from = fromDate.format(DateTimeFormatter.ISO_DATE);
        String to = toDate.format(DateTimeFormatter.ISO_DATE);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/aggs/ticker/{symbol}/range/1/day/{from}/{to}")
                        .queryParam("apiKey", apiKey)
                        .build(symbol, from, to))
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.TOO_MANY_REQUESTS),
                        response -> Mono.error(new PolygonApiException("API rate limit exceeded")))
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new PolygonApiException("Polygon API error: " + error))))
                .bodyToMono(PolygonApiResponse.class);
    }
} 