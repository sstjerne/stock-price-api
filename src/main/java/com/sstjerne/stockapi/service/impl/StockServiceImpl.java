package com.sstjerne.stockapi.service.impl;

import com.sstjerne.stockapi.client.PolygonApiClient;
import com.sstjerne.stockapi.dto.StockResponse;
import com.sstjerne.stockapi.entity.Stock;
import com.sstjerne.stockapi.exception.StockNotFoundException;
import com.sstjerne.stockapi.metrics.StockMetrics;
import com.sstjerne.stockapi.repository.StockRepository;
import com.sstjerne.stockapi.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final PolygonApiClient polygonApiClient;
    private final StockMetrics stockMetrics;

    @Override
    @Transactional
    public List<StockResponse> fetchAndSaveStockData(String companySymbol, LocalDate fromDate, LocalDate toDate) {
        stockMetrics.incrementStockFetch();
        
        return polygonApiClient.getStockData(companySymbol, fromDate, toDate)
                .map(response -> response.getResults().stream()
                        .map(result -> {
                            Stock stock = new Stock();
                            stock.setCompanySymbol(companySymbol);
                            stock.setDate(Instant.ofEpochMilli(result.getTimestamp())
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate());
                            stock.setOpenPrice(result.getOpenPrice());
                            stock.setClosePrice(result.getClosePrice());
                            stock.setHighPrice(result.getHighPrice());
                            stock.setLowPrice(result.getLowPrice());
                            stock.setVolume(result.getVolume());
                            Stock savedStock = stockRepository.save(stock);
                            stockMetrics.incrementStockSave();
                            return savedStock;
                        })
                        .map(this::mapToResponse)
                        .collect(Collectors.toList()))
                .block();
    }

    @Override
    public StockResponse getStockBySymbolAndDate(String companySymbol, LocalDate date) {
        return stockRepository.findByCompanySymbolAndDate(companySymbol, date)
                .map(this::mapToResponse)
                .orElseThrow(() -> {
                    stockMetrics.incrementStockNotFound();
                    return new StockNotFoundException("Stock not found");
                });
    }

    private StockResponse mapToResponse(Stock stock) {
        return StockResponse.builder()
                .companySymbol(stock.getCompanySymbol())
                .date(stock.getDate())
                .openPrice(stock.getOpenPrice())
                .closePrice(stock.getClosePrice())
                .highPrice(stock.getHighPrice())
                .lowPrice(stock.getLowPrice())
                .volume(stock.getVolume())
                .build();
    }
} 