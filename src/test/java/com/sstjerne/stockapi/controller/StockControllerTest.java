package com.sstjerne.stockapi.controller;

import com.sstjerne.stockapi.dto.StockResponse;
import com.sstjerne.stockapi.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    private String validSymbol;
    private LocalDate validDate;
    private LocalDate validFromDate;
    private LocalDate validToDate;
    private StockResponse mockStockResponse;

    @BeforeEach
    void setUp() {
        validSymbol = "AAPL";
        validDate = LocalDate.now();
        validFromDate = LocalDate.now().minusDays(7);
        validToDate = LocalDate.now();

        mockStockResponse = new StockResponse();
        mockStockResponse.setCompanySymbol(validSymbol);
        mockStockResponse.setDate(validDate);
        mockStockResponse.setOpenPrice(150.0);
        mockStockResponse.setClosePrice(155.0);
        mockStockResponse.setHighPrice(160.0);
        mockStockResponse.setLowPrice(145.0);
        mockStockResponse.setVolume(1000L);
    }

    @Test
    void fetchStockData_WithValidParameters_ReturnsOk() throws Exception {
        // Arrange
        List<StockResponse> expectedResponse = Arrays.asList(mockStockResponse);
        when(stockService.fetchAndSaveStockData(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/stocks/fetch")
                .param("companySymbol", validSymbol)
                .param("fromDate", validFromDate.toString())
                .param("toDate", validToDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].companySymbol").value(validSymbol))
                .andExpect(jsonPath("$[0].openPrice").value(150.0))
                .andExpect(jsonPath("$[0].closePrice").value(155.0));
    }

    @Test
    void fetchStockData_WithMissingCompanySymbol_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/stocks/fetch")
                .param("fromDate", validFromDate.toString())
                .param("toDate", validToDate.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.details.companySymbol").value("Required parameter 'companySymbol' is not present"));
    }

    @Test
    void fetchStockData_WithMissingDates_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/stocks/fetch")
                .param("companySymbol", validSymbol))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    void fetchStockData_WithInvalidDateRange_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/stocks/fetch")
                .param("companySymbol", validSymbol)
                .param("fromDate", validToDate.toString())
                .param("toDate", validFromDate.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.details.error").value("toDate must be after fromDate"));
    }

    @Test
    void getStockBySymbolAndDate_WithValidParameters_ReturnsOk() throws Exception {
        // Arrange
        when(stockService.getStockBySymbolAndDate(anyString(), any(LocalDate.class)))
                .thenReturn(mockStockResponse);

        // Act & Assert
        mockMvc.perform(get("/stocks/{companySymbol}", validSymbol)
                .param("date", validDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.companySymbol").value(validSymbol))
                .andExpect(jsonPath("$.openPrice").value(150.0))
                .andExpect(jsonPath("$.closePrice").value(155.0));
    }

    @Test
    void getStockBySymbolAndDate_WithMissingDate_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/stocks/{companySymbol}", validSymbol))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.details.date").value("Required parameter 'date' is not present"));
    }

    @Test
    void getStockBySymbolAndDate_WithEmptySymbol_ReturnsMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/stocks", "")
                .param("date", validDate.toString()))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"))
                .andExpect(jsonPath("$.details").doesNotExist());
    }

    @Test
    void nonMappedRequest_ReturnsMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/non-existent-path"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"))
                .andExpect(jsonPath("$.details").doesNotExist());
    }

    @Test
    void nonMappedMethod_ReturnsMethodNotAllowed() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/stocks/AAPL")
                .param("date", validDate.toString()))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"))
                .andExpect(jsonPath("$.details").doesNotExist());
    }
} 