package com.sstjerne.stockapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PolygonApiResponse {
    private String ticker;
    private int queryCount;
    private int resultsCount;
    private boolean adjusted;
    private List<Result> results;

    @Data
    public static class Result {
        @JsonProperty("v")
        private long volume;
        @JsonProperty("vw")
        private double volumeWeightedAveragePrice;
        @JsonProperty("o")
        private double openPrice;
        @JsonProperty("c")
        private double closePrice;
        @JsonProperty("h")
        private double highPrice;
        @JsonProperty("l")
        private double lowPrice;
        @JsonProperty("t")
        private long timestamp;
    }
} 