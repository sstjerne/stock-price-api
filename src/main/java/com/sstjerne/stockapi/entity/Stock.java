package com.sstjerne.stockapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "stocks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"company_symbol", "date"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(StockId.class)
public class Stock {
    @Id
    @Column(name = "company_symbol", nullable = false)
    private String companySymbol;

    @Id
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "open_price", nullable = false)
    private Double openPrice;

    @Column(name = "close_price", nullable = false)
    private Double closePrice;

    @Column(name = "high_price", nullable = false)
    private Double highPrice;

    @Column(name = "low_price", nullable = false)
    private Double lowPrice;

    @Column(name = "volume", nullable = false)
    private Long volume;
} 