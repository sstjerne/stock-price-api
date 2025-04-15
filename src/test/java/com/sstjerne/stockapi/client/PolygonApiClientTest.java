package com.sstjerne.stockapi.client;

import com.sstjerne.stockapi.dto.PolygonApiResponse;
import com.sstjerne.stockapi.exception.PolygonApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.LocalDate;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolygonApiClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private PolygonApiClient polygonApiClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(polygonApiClient, "apiKey", "test-api-key");
        
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri((Function<UriBuilder, URI>) any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void getStockData_Success() {
        // Arrange
        PolygonApiResponse expectedResponse = new PolygonApiResponse();
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PolygonApiResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Act & Assert
        StepVerifier.create(polygonApiClient.getStockData("AAPL", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getStockData_RateLimitExceeded() {
        // Arrange
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PolygonApiResponse.class))
                .thenReturn(Mono.error(new PolygonApiException("API rate limit exceeded")));

        // Act & Assert
        StepVerifier.create(polygonApiClient.getStockData("AAPL", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof PolygonApiException && 
                    throwable.getMessage().equals("API rate limit exceeded"))
                .verify();
    }

    @Test
    void getStockData_ApiError() {
        // Arrange
        String errorMessage = "Invalid API Key";
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PolygonApiResponse.class))
                .thenReturn(Mono.error(new PolygonApiException("Polygon API error: " + errorMessage)));

        // Act & Assert
        StepVerifier.create(polygonApiClient.getStockData("AAPL", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof PolygonApiException && 
                    throwable.getMessage().equals("Polygon API error: " + errorMessage))
                .verify();
    }

    @Test
    void getStockData_InvalidSymbol() {
        // Arrange
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PolygonApiResponse.class))
                .thenReturn(Mono.error(new PolygonApiException("Polygon API error: Invalid symbol")));

        // Act & Assert
        StepVerifier.create(polygonApiClient.getStockData("INVALID", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof PolygonApiException && 
                    throwable.getMessage().contains("Invalid symbol"))
                .verify();
    }
} 