package com.sstjerne.stockapi.exception;

public class PolygonApiException extends RuntimeException {
    public PolygonApiException(String message) {
        super(message);
    }

    public PolygonApiException(String message, Throwable cause) {
        super(message, cause);
    }
} 