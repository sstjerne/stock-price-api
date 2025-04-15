package com.sstjerne.stockapi.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class StockMetrics {
    private final Counter stockFetchCounter;
    private final Counter stockSaveCounter;
    private final Counter stockNotFoundCounter;

    public StockMetrics(MeterRegistry registry) {
        this.stockFetchCounter = Counter.builder("stock.fetch.count")
                .description("Number of stock data fetch operations")
                .register(registry);
        
        this.stockSaveCounter = Counter.builder("stock.save.count")
                .description("Number of stock data save operations")
                .register(registry);
        
        this.stockNotFoundCounter = Counter.builder("stock.notfound.count")
                .description("Number of stock not found errors")
                .register(registry);
    }

    public void incrementStockFetch() {
        stockFetchCounter.increment();
    }

    public void incrementStockSave() {
        stockSaveCounter.increment();
    }

    public void incrementStockNotFound() {
        stockNotFoundCounter.increment();
    }
} 