package com.sstjerne.stockapi.service.impl;

import com.sstjerne.stockapi.client.PolygonApiClient;
import com.sstjerne.stockapi.dto.PolygonApiResponse;
import com.sstjerne.stockapi.dto.StockResponse;
import com.sstjerne.stockapi.entity.Stock;
import com.sstjerne.stockapi.exception.StockNotFoundException;
import com.sstjerne.stockapi.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
class StockServiceImplIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13-bullseye")
            .withDatabaseName("stockdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private StockServiceImpl stockService;

    @Autowired
    private StockRepository stockRepository;

    @MockBean
    private PolygonApiClient polygonApiClient;

    private final String companySymbol = "AAPL";
    private final LocalDate fromDate = LocalDate.of(2024, 1, 1);
    private final LocalDate toDate = LocalDate.of(2024, 1, 2);

    @BeforeEach
    void setUp() {
        stockRepository.deleteAll();
    }

    @Test
    void fetchAndSaveStockData_ShouldSaveToDatabase() {
        // Arrange
        PolygonApiResponse response = new PolygonApiResponse();
        response.setTicker(companySymbol);
        response.setResults(List.of(
                createResult(174.25, 176.10, 178.00, 172.80, 58900000L, 1704112172000L)
        ));

        when(polygonApiClient.getStockData(any(), any(), any()))
                .thenReturn(Mono.just(response));

        // Act
        List<StockResponse> responses = stockService.fetchAndSaveStockData(companySymbol, fromDate, toDate);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        
        // Verify database state
        List<Stock> savedStocks = stockRepository.findByCompanySymbol(companySymbol);
        assertEquals(1, savedStocks.size());
        
        Stock savedStock = savedStocks.get(0);
        assertEquals(companySymbol, savedStock.getCompanySymbol());
        assertEquals(LocalDate.of(2024, 1, 1), savedStock.getDate());
        assertEquals(174.25, savedStock.getOpenPrice());
        assertEquals(176.10, savedStock.getClosePrice());
        assertEquals(178.00, savedStock.getHighPrice());
        assertEquals(172.80, savedStock.getLowPrice());
        assertEquals(58900000L, savedStock.getVolume());
    }

    @Test
    void getStockBySymbolAndDate_ShouldReturnSavedStock() {
        // Arrange
        Stock stock = new Stock();
        stock.setCompanySymbol(companySymbol);
        stock.setDate(fromDate);
        stock.setOpenPrice(174.25);
        stock.setClosePrice(176.10);
        stock.setHighPrice(178.00);
        stock.setLowPrice(172.80);
        stock.setVolume(58900000L);
        
        stockRepository.save(stock);

        // Act
        StockResponse response = stockService.getStockBySymbolAndDate(companySymbol, fromDate);

        // Assert
        assertNotNull(response);
        assertEquals(companySymbol, response.getCompanySymbol());
        assertEquals(fromDate, response.getDate());
        assertEquals(174.25, response.getOpenPrice());
        assertEquals(176.10, response.getClosePrice());
        assertEquals(178.00, response.getHighPrice());
        assertEquals(172.80, response.getLowPrice());
        assertEquals(58900000L, response.getVolume());
    }

    @Test
    void getStockBySymbolAndDate_WhenStockNotFound_ShouldThrowException() {
        // Act & Assert
        assertThrows(StockNotFoundException.class, () ->
                stockService.getStockBySymbolAndDate(companySymbol, fromDate));
    }

    @Test
    void fetchAndSaveStockData_WhenStockExists_ShouldUpdateExistingRecord() {
        // Arrange - First save a stock
        Stock existingStock = new Stock();
        existingStock.setCompanySymbol(companySymbol);
        existingStock.setDate(fromDate);
        existingStock.setOpenPrice(100.0);
        existingStock.setClosePrice(105.0);
        existingStock.setHighPrice(110.0);
        existingStock.setLowPrice(95.0);
        existingStock.setVolume(1000L);
        stockRepository.save(existingStock);

        // Verify initial state
        List<Stock> initialStocks = stockRepository.findByCompanySymbol(companySymbol);
        assertEquals(1, initialStocks.size());
        assertEquals(100.0, initialStocks.get(0).getOpenPrice());

        // Prepare new data for the same stock
        PolygonApiResponse response = new PolygonApiResponse();
        response.setTicker(companySymbol);
        response.setResults(List.of(
                createResult(174.25, 176.10, 178.00, 172.80, 58900000L, 1704112172000L)
        ));

        when(polygonApiClient.getStockData(any(), any(), any()))
                .thenReturn(Mono.just(response));

        // Act - Try to save the same stock with updated values
        List<StockResponse> responses = stockService.fetchAndSaveStockData(companySymbol, fromDate, toDate);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        
        // Verify database state - should have updated the existing record
        List<Stock> updatedStocks = stockRepository.findByCompanySymbol(companySymbol);
        assertEquals(1, updatedStocks.size()); // Still only one record
        
        Stock updatedStock = updatedStocks.get(0);
        assertEquals(companySymbol, updatedStock.getCompanySymbol());
        assertEquals(fromDate, updatedStock.getDate());
        assertEquals(174.25, updatedStock.getOpenPrice()); // Updated value
        assertEquals(176.10, updatedStock.getClosePrice());
        assertEquals(178.00, updatedStock.getHighPrice());
        assertEquals(172.80, updatedStock.getLowPrice());
        assertEquals(58900000L, updatedStock.getVolume());
    }

    private PolygonApiResponse.Result createResult(double open, double close, double high, double low, long volume, long timestamp) {
        PolygonApiResponse.Result result = new PolygonApiResponse.Result();
        result.setOpenPrice(open);
        result.setClosePrice(close);
        result.setHighPrice(high);
        result.setLowPrice(low);
        result.setVolume(volume);
        result.setTimestamp(timestamp);
        return result;
    }
} 