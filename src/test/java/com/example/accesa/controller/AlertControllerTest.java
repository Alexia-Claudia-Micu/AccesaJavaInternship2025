package com.example.accesa.controller;

import com.example.accesa.domain.PriceAlert;
import com.example.accesa.dto.PriceAlertRequest;
import com.example.accesa.service.AlertService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertController.class)
class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertService alertService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- /alerts ---

    @Test
    void createAlert_withValidInput_returnsCreatedAlert() throws Exception {
        PriceAlertRequest request = new PriceAlertRequest("P001", "Lidl", BigDecimal.valueOf(9.99));
        PriceAlert alert = new PriceAlert(1L, "P001", "Lidl", BigDecimal.valueOf(9.99), LocalDate.now());

        Mockito.when(alertService.createAlert(any())).thenReturn(alert);

        mockMvc.perform(post("/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productId").value("P001"))
                .andExpect(jsonPath("$.data.storeName").value("Lidl"));
    }

    @Test
    void createAlert_withMissingFields_returnsBadRequest() throws Exception {
        String invalidJson = """
                {
                  "storeName": "Lidl"
                }
                """;

        mockMvc.perform(post("/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createAlert_withMalformedJson_returnsBadRequest() throws Exception {
        String badJson = """
                {
                    "productId": ["P001"],
                    "storeName": "Lidl",
                    "targetPrice": 9.99
                }
                """;

        mockMvc.perform(post("/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    // --- /alerts/active ---

    @Test
    void getActiveAlerts_returnsListOfAlerts() throws Exception {
        List<PriceAlert> alerts = List.of(
                new PriceAlert(1L, "P001", "Lidl", BigDecimal.valueOf(10), LocalDate.now())
        );

        Mockito.when(alertService.getActiveAlerts()).thenReturn(alerts);

        mockMvc.perform(get("/alerts/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].productId").value("P001"));
    }

    @Test
    void getActiveAlerts_returnsEmptyList() throws Exception {
        Mockito.when(alertService.getActiveAlerts()).thenReturn(List.of());

        mockMvc.perform(get("/alerts/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}