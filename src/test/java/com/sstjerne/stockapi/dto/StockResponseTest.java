package com.sstjerne.stockapi.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StockResponseTest {

    @Test
    void testNoArgsConstructor() {
        // Act
        StockResponse response = new StockResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getCompanySymbol());
        assertNull(response.getDate());
        assertNull(response.getOpenPrice());
        assertNull(response.getClosePrice());
        assertNull(response.getHighPrice());
        assertNull(response.getLowPrice());
        assertNull(response.getVolume());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String companySymbol = "AAPL";
        LocalDate date = LocalDate.of(2024, 1, 1);
        Double openPrice = 175.0;
        Double closePrice = 176.0;
        Double highPrice = 177.0;
        Double lowPrice = 174.0;
        Long volume = 1000000L;

        // Act
        StockResponse response = new StockResponse(companySymbol, date, openPrice, closePrice, highPrice, lowPrice, volume);

        // Assert
        assertEquals(companySymbol, response.getCompanySymbol());
        assertEquals(date, response.getDate());
        assertEquals(openPrice, response.getOpenPrice());
        assertEquals(closePrice, response.getClosePrice());
        assertEquals(highPrice, response.getHighPrice());
        assertEquals(lowPrice, response.getLowPrice());
        assertEquals(volume, response.getVolume());
    }

    @Test
    void testBuilder() {
        // Arrange
        String companySymbol = "GOOGL";
        LocalDate date = LocalDate.of(2024, 1, 2);
        Double openPrice = 140.0;
        Double closePrice = 141.0;
        Double highPrice = 142.0;
        Double lowPrice = 139.0;
        Long volume = 2000000L;

        // Act
        StockResponse response = StockResponse.builder()
                .companySymbol(companySymbol)
                .date(date)
                .openPrice(openPrice)
                .closePrice(closePrice)
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .volume(volume)
                .build();

        // Assert
        assertEquals(companySymbol, response.getCompanySymbol());
        assertEquals(date, response.getDate());
        assertEquals(openPrice, response.getOpenPrice());
        assertEquals(closePrice, response.getClosePrice());
        assertEquals(highPrice, response.getHighPrice());
        assertEquals(lowPrice, response.getLowPrice());
        assertEquals(volume, response.getVolume());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        StockResponse response = new StockResponse();
        String companySymbol = "MSFT";
        LocalDate date = LocalDate.of(2024, 1, 3);
        Double openPrice = 350.0;
        Double closePrice = 351.0;
        Double highPrice = 352.0;
        Double lowPrice = 349.0;
        Long volume = 3000000L;

        // Act
        response.setCompanySymbol(companySymbol);
        response.setDate(date);
        response.setOpenPrice(openPrice);
        response.setClosePrice(closePrice);
        response.setHighPrice(highPrice);
        response.setLowPrice(lowPrice);
        response.setVolume(volume);

        // Assert
        assertEquals(companySymbol, response.getCompanySymbol());
        assertEquals(date, response.getDate());
        assertEquals(openPrice, response.getOpenPrice());
        assertEquals(closePrice, response.getClosePrice());
        assertEquals(highPrice, response.getHighPrice());
        assertEquals(lowPrice, response.getLowPrice());
        assertEquals(volume, response.getVolume());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        StockResponse response1 = new StockResponse("AAPL", LocalDate.of(2024, 1, 1), 175.0, 176.0, 177.0, 174.0, 1000000L);
        StockResponse response2 = new StockResponse("AAPL", LocalDate.of(2024, 1, 1), 175.0, 176.0, 177.0, 174.0, 1000000L);
        StockResponse response3 = new StockResponse("GOOGL", LocalDate.of(2024, 1, 1), 175.0, 176.0, 177.0, 174.0, 1000000L);

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        StockResponse response = new StockResponse("AAPL", LocalDate.of(2024, 1, 1), 175.0, 176.0, 177.0, 174.0, 1000000L);

        // Act
        String toString = response.toString();

        // Assert
        assertTrue(toString.contains("companySymbol=AAPL"));
        assertTrue(toString.contains("date=2024-01-01"));
        assertTrue(toString.contains("openPrice=175.0"));
        assertTrue(toString.contains("closePrice=176.0"));
        assertTrue(toString.contains("highPrice=177.0"));
        assertTrue(toString.contains("lowPrice=174.0"));
        assertTrue(toString.contains("volume=1000000"));
    }
} 