package com.sstjerne.stockapi.repository;

import com.sstjerne.stockapi.entity.Stock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
public class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Test
    void whenSavingStockWithSameCompanySymbolAndDate_thenShouldUpdateExistingRecord() {
        // Given
        LocalDate date = LocalDate.of(2024, 1, 1);
        String companySymbol = "AAPL";
        
        Stock initialStock = new Stock();
        initialStock.setCompanySymbol(companySymbol);
        initialStock.setDate(date);
        initialStock.setOpenPrice(100.0);
        initialStock.setClosePrice(105.0);
        initialStock.setHighPrice(110.0);
        initialStock.setLowPrice(95.0);
        initialStock.setVolume(1000L);

        // When - Save initial stock
        Stock savedInitialStock = stockRepository.save(initialStock);

        // Then - Verify initial stock was saved
        Optional<Stock> foundStockOpt = stockRepository.findByCompanySymbolAndDate(companySymbol, date);
        assertTrue(foundStockOpt.isPresent());
        Stock foundStock = foundStockOpt.get();
        assertEquals(100.0, foundStock.getOpenPrice());
        assertEquals(105.0, foundStock.getClosePrice());

        // Given - Create stock with same company symbol and date but different values
        Stock updatedStock = new Stock();
        updatedStock.setCompanySymbol(companySymbol);
        updatedStock.setDate(date);
        updatedStock.setOpenPrice(200.0);
        updatedStock.setClosePrice(205.0);
        updatedStock.setHighPrice(210.0);
        updatedStock.setLowPrice(195.0);
        updatedStock.setVolume(2000L);

        // When - Save updated stock
        Stock savedUpdatedStock = stockRepository.save(updatedStock);

        // Then - Verify the stock was updated instead of creating a new one
        Optional<Stock> retrievedStockOpt = stockRepository.findByCompanySymbolAndDate(companySymbol, date);
        assertTrue(retrievedStockOpt.isPresent());
        Stock retrievedStock = retrievedStockOpt.get();
        assertEquals(200.0, retrievedStock.getOpenPrice());
        assertEquals(205.0, retrievedStock.getClosePrice());
        assertEquals(210.0, retrievedStock.getHighPrice());
        assertEquals(195.0, retrievedStock.getLowPrice());
        assertEquals(2000L, retrievedStock.getVolume());

        // Verify only one record exists for this company symbol and date
        assertEquals(1, stockRepository.findByCompanySymbol(companySymbol).size());
        
        // Verify the IDs are the same (same record was updated)
        assertEquals(savedInitialStock.getCompanySymbol(), savedUpdatedStock.getCompanySymbol());
        assertEquals(savedInitialStock.getDate(), savedUpdatedStock.getDate());
    }

    @Test
    void whenSavingDifferentStocks_thenShouldCreateNewRecords() {
        // Given
        LocalDate date1 = LocalDate.of(2024, 1, 1);
        LocalDate date2 = LocalDate.of(2024, 1, 2);
        String companySymbol = "AAPL";
        
        Stock stock1 = new Stock();
        stock1.setCompanySymbol(companySymbol);
        stock1.setDate(date1);
        stock1.setOpenPrice(100.0);
        stock1.setClosePrice(105.0);
        stock1.setHighPrice(110.0);
        stock1.setLowPrice(95.0);
        stock1.setVolume(1000L);

        Stock stock2 = new Stock();
        stock2.setCompanySymbol(companySymbol);
        stock2.setDate(date2);
        stock2.setOpenPrice(200.0);
        stock2.setClosePrice(205.0);
        stock2.setHighPrice(210.0);
        stock2.setLowPrice(195.0);
        stock2.setVolume(2000L);

        // When
        Stock savedStock1 = stockRepository.save(stock1);
        Stock savedStock2 = stockRepository.save(stock2);

        // Then
        assertEquals(2, stockRepository.findByCompanySymbol(companySymbol).size());
        
        Optional<Stock> retrievedStock1Opt = stockRepository.findByCompanySymbolAndDate(companySymbol, date1);
        Optional<Stock> retrievedStock2Opt = stockRepository.findByCompanySymbolAndDate(companySymbol, date2);
        
        assertTrue(retrievedStock1Opt.isPresent());
        assertTrue(retrievedStock2Opt.isPresent());
        
        Stock retrievedStock1 = retrievedStock1Opt.get();
        Stock retrievedStock2 = retrievedStock2Opt.get();
        
        assertEquals(100.0, retrievedStock1.getOpenPrice());
        assertEquals(200.0, retrievedStock2.getOpenPrice());
        
        // Verify different composite keys
        assertEquals(date1, retrievedStock1.getDate());
        assertEquals(date2, retrievedStock2.getDate());
    }
} 