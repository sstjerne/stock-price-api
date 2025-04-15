package com.sstjerne.stockapi.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.sstjerne.stockapi.dto.ErrorResponse;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleStockNotFoundException_ShouldReturnNotFoundStatus() {
        // Arrange
        StockNotFoundException ex = new StockNotFoundException("Stock not found");

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleStockNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Stock not found", body.get("message"));
        assertEquals(HttpStatus.NOT_FOUND.value(), body.get("status"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handlePolygonApiException_ShouldReturnServiceUnavailableStatus() {
        // Arrange
        PolygonApiException ex = new PolygonApiException("API error");

        // Act
        ResponseEntity<Object> response = exceptionHandler.handlePolygonApiException(ex);

        // Assert
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("API error", body.get("message"));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), body.get("status"));
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestStatus() {
        // Arrange
        ValidationException ex = new ValidationException("Invalid input");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Validation Error", errorResponse.getError());
        assertEquals("Invalid input", errorResponse.getDetails().get("error"));
    }

    @Test
    void handleMissingServletRequestParameter_ShouldReturnBadRequestStatus() {
        // Arrange
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("date", "LocalDate");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMissingServletRequestParameter(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertTrue(errorResponse.getDetails().containsKey("date"));
    }

    @Test
    void handleMethodArgumentTypeMismatch_ShouldReturnMethodNotAllowedStatus() {
        // Arrange
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentTypeMismatch(ex);

        // Assert
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), errorResponse.getStatus());
        assertEquals("Method Not Allowed", errorResponse.getError());
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerError() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals("An unexpected error occurred", errorResponse.getDetails().get("error"));
    }
} 