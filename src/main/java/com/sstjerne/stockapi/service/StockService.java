package com.sstjerne.stockapi.service;

import com.sstjerne.stockapi.dto.StockResponse;

import java.time.LocalDate;
import java.util.List;

public interface StockService {
    List<StockResponse> fetchAndSaveStockData(String companySymbol, LocalDate fromDate, LocalDate toDate);
    StockResponse getStockBySymbolAndDate(String companySymbol, LocalDate date);
} 