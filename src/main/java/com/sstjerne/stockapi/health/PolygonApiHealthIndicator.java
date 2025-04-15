package com.sstjerne.stockapi.health;

import com.sstjerne.stockapi.client.PolygonApiClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Component
public class PolygonApiHealthIndicator implements HealthIndicator {

    private final PolygonApiClient polygonApiClient;

    public PolygonApiHealthIndicator(PolygonApiClient polygonApiClient) {
        this.polygonApiClient = polygonApiClient;
    }

    @Override
    public Health health() {
        try {
            // Try to fetch stock data for a known symbol (AAPL) to check API availability
            LocalDate today = LocalDate.now();
            Mono<Boolean> apiAvailable = polygonApiClient.getStockData("AAPL", today, today)
                    .map(response -> true)
                    .onErrorReturn(false);

            Boolean isAvailable = apiAvailable.block();
            
            if (Boolean.TRUE.equals(isAvailable)) {
                return Health.up()
                        .withDetail("status", "API is available")
                        .build();
            } else {
                return Health.down()
                        .withDetail("status", "API is not responding")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
} 