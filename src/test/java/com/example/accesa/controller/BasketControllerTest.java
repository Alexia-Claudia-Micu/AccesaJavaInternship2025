package com.example.accesa.controller;

import com.example.accesa.dto.BasketRequest;
import com.example.accesa.service.BasketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BasketController.class)
class BasketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BasketService basketService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- /basket/split-optimize ---

    @Test
    void splitOptimizeBasket_shouldReturnSplitResult() throws Exception {
        BasketRequest request = new BasketRequest(List.of("P001", "P003", "P009"));
        Map<String, Object> response = Map.of(
                "total", new BigDecimal("30.10"),
                "items", List.of(
                        Map.of("productId", "P001", "store", "Lidl", "price", new BigDecimal("10.00")),
                        Map.of("productId", "P003", "store", "Profi", "price", new BigDecimal("10.10")),
                        Map.of("productId", "P009", "store", "Kaufland", "price", new BigDecimal("10.00"))
                )
        );

        when(basketService.splitOptimizeBasket(request)).thenReturn(response);

        mockMvc.perform(post("/basket/split-optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").value(30.10))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].productId").value("P001"));
    }

    @Test
    void splitOptimize_withEmptyProductList_shouldReturnBadRequest() throws Exception {
        BasketRequest request = new BasketRequest(List.of());

        mockMvc.perform(post("/basket/split-optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // --- /basket/optimize ---

    @Test
    void optimizeBasket_withEmptyProductList_shouldReturnBadRequest() throws Exception {
        BasketRequest request = new BasketRequest(List.of());

        mockMvc.perform(post("/basket/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}