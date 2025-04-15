package com.sstjerne.stockapi.controller;

import com.sstjerne.stockapi.dto.StockResponse;
import com.sstjerne.stockapi.exception.ValidationException;
import com.sstjerne.stockapi.service.StockService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/stocks")
@Validated
public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping("/fetch")
    public ResponseEntity<List<StockResponse>> fetchStockData(
        @RequestParam @NotBlank(message = "Company symbol is required")
        String companySymbol,
        @RequestParam
        @NotNull(message = "From date is required")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate fromDate,
        @RequestParam
        @NotNull(message = "To date is required")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate toDate) {
        
        if (toDate.isBefore(fromDate)) {
            throw new ValidationException("toDate must be after fromDate");
        }

        return ResponseEntity.ok(stockService.fetchAndSaveStockData(companySymbol, fromDate, toDate));
    }

    @GetMapping("/{companySymbol}")
    public ResponseEntity<StockResponse> getStockBySymbolAndDate(
            @PathVariable @NotBlank(message = "Company symbol is required") String companySymbol,
            @RequestParam @NotNull(message = "Date is required") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        return ResponseEntity.ok(stockService.getStockBySymbolAndDate(companySymbol, date));
    }
} 