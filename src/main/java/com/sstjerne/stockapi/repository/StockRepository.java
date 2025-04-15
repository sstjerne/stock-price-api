package com.sstjerne.stockapi.repository;

import com.sstjerne.stockapi.entity.Stock;
import com.sstjerne.stockapi.entity.StockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, StockId> {
    Optional<Stock> findByCompanySymbolAndDate(String companySymbol, LocalDate date);
    List<Stock> findByCompanySymbol(String companySymbol);
} 