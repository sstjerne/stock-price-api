package com.sstjerne.stockapi.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @Test
    void testNoArgsConstructor() {
        // Act
        Stock stock = new Stock();

        // Assert
        assertNotNull(stock);
        assertNull(stock.getCompanySymbol());
        assertNull(stock.getDate());
        assertNull(stock.getOpenPrice());
        assertNull(stock.getClosePrice());
        assertNull(stock.getHighPrice());
        assertNull(stock.getLowPrice());
        assertNull(stock.getVolume());
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
        Stock stock = new Stock(companySymbol, date, openPrice, closePrice, highPrice, lowPrice, volume);

        // Assert
        assertEquals(companySymbol, stock.getCompanySymbol());
        assertEquals(date, stock.getDate());
        assertEquals(openPrice, stock.getOpenPrice());
        assertEquals(closePrice, stock.getClosePrice());
        assertEquals(highPrice, stock.getHighPrice());
        assertEquals(lowPrice, stock.getLowPrice());
        assertEquals(volume, stock.getVolume());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        Stock stock = new Stock();
        String companySymbol = "GOOGL";
        LocalDate date = LocalDate.of(2024, 1, 2);
        Double openPrice = 140.0;
        Double closePrice = 141.0;
        Double highPrice = 142.0;
        Double lowPrice = 139.0;
        Long volume = 2000000L;

        // Act
        stock.setCompanySymbol(companySymbol);
        stock.setDate(date);
        stock.setOpenPrice(openPrice);
        stock.setClosePrice(closePrice);
        stock.setHighPrice(highPrice);
        stock.setLowPrice(lowPrice);
        stock.setVolume(volume);

        // Assert
        assertEquals(companySymbol, stock.getCompanySymbol());
        assertEquals(date, stock.getDate());
        assertEquals(openPrice, stock.getOpenPrice());
        assertEquals(closePrice, stock.getClosePrice());
        assertEquals(highPrice, stock.getHighPrice());
        assertEquals(lowPrice, stock.getLowPrice());
        assertEquals(volume, stock.getVolume());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Stock stock1 = new Stock("AAPL", LocalDate.of(2024, 1, 1), 175.0, 176.0, 177.0, 174.0, 1000000L);
        Stock stock2 = new Stock("AAPL", LocalDate.of(2024, 1, 1), 175.0, 176.0, 177.0, 174.0, 1000000L);
        Stock stock3 = new Stock("GOOGL", LocalDate.of(2024, 1, 1), 175.0, 176.0, 177.0, 174.0, 1000000L);

        // Assert
        assertEquals(stock1, stock2);
        assertEquals(stock1.hashCode(), stock2.hashCode());
        assertNotEquals(stock1, stock3);
        assertNotEquals(stock1.hashCode(), stock3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        Stock stock = new Stock("AAPL", LocalDate.of(2024, 1, 1), 175.0, 176.0, 177.0, 174.0, 1000000L);

        // Act
        String toString = stock.toString();

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