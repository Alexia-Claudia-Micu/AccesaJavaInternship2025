package com.example.accesa.exception;

import com.example.accesa.controller.AlertController;
import com.example.accesa.dto.PriceAlertRequest;
import com.example.accesa.service.AlertService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlertService alertService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenValidationFails_returnBadRequest() throws Exception {
        // Missing required fields
        String json = """
            {
              "storeName": "Lidl"
            }
        """;

        mockMvc.perform(post("/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void whenJsonIsMalformed_returnBadRequest() throws Exception {
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

    @Test
    void whenServiceThrowsGenericException_return500() throws Exception {
        PriceAlertRequest validRequest = new PriceAlertRequest("P001", "Lidl", BigDecimal.valueOf(9.99));
        Mockito.when(alertService.createAlert(any())).thenThrow(new RuntimeException("Boom"));

        mockMvc.perform(post("/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Boom"));
    }

    @Test
    void whenQueryParamViolatesConstraint_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/discounts/best?top=-5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }
}
