package com.sstjerne.stockapi.service.impl;

import com.sstjerne.stockapi.client.PolygonApiClient;
import com.sstjerne.stockapi.dto.PolygonApiResponse;
import com.sstjerne.stockapi.dto.StockResponse;
import com.sstjerne.stockapi.entity.Stock;
import com.sstjerne.stockapi.exception.StockNotFoundException;
import com.sstjerne.stockapi.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private PolygonApiClient polygonApiClient;

    @InjectMocks
    private StockServiceImpl stockService;

    private PolygonApiResponse polygonApiResponse;
    private Stock stock;

    String companySymbol = "AAPL";
    LocalDate fromDate = LocalDate.of(2024, 1, 1);
    LocalDate toDate = LocalDate.of(2024, 1, 2);

    @BeforeEach
    void setUp() {


        polygonApiResponse = new PolygonApiResponse();
        polygonApiResponse.setTicker("AAPL");
        polygonApiResponse.setResults(List.of(
                createResult(174.25, 176.10, 178.00, 172.80, 58900000L, 1704067200000L)
        ));

        stock = new Stock();
        stock.setCompanySymbol("AAPL");
        stock.setDate(LocalDate.of(2024, 1, 1));
        stock.setOpenPrice(174.25);
        stock.setClosePrice(176.10);
        stock.setHighPrice(178.00);
        stock.setLowPrice(172.80);
        stock.setVolume(58900000L);
    }

    @Test
    void fetchAndSaveStockData_Success() {
        when(polygonApiClient.getStockData(any(), any(), any()))
                .thenReturn(Mono.just(polygonApiResponse));
        when(stockRepository.save(any())).thenReturn(stock);

        List<StockResponse> responses = stockService.fetchAndSaveStockData(companySymbol, fromDate, toDate);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(stockRepository, times(1)).save(any());
    }

    @Test
    void getStockBySymbolAndDate_Success() {
        when(stockRepository.findByCompanySymbolAndDate("AAPL", LocalDate.of(2024, 1, 1)))
                .thenReturn(Optional.of(stock));


        StockResponse response = stockService.getStockBySymbolAndDate(companySymbol, fromDate);

        assertNotNull(response);
        assertEquals("AAPL", response.getCompanySymbol());
        assertEquals(LocalDate.of(2024, 1, 1), response.getDate());
        assertEquals(174.25, response.getOpenPrice());
    }

    @Test
    void getStockBySymbolAndDate_NotFound() {



        when(stockRepository.findByCompanySymbolAndDate("AAPL", LocalDate.of(2024, 1, 1)))
                .thenReturn(Optional.empty());

        assertThrows(StockNotFoundException.class, () ->
                stockService.getStockBySymbolAndDate(companySymbol, fromDate));
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