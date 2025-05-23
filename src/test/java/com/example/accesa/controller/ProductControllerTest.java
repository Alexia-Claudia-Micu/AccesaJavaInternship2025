package com.example.accesa.controller;

import com.example.accesa.domain.Product;
import com.example.accesa.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product mockProduct() {
        Product product = new Product();
        product.setName("Milk");
        product.setBrand("BrandA");
        product.setCategory("Dairy");
        product.setPrice(BigDecimal.valueOf(4.99));
        return product;
    }

    // --- /products ---

    @Test
    void getAllProducts_shouldReturnList() throws Exception {
        when(productService.getAllProducts(null)).thenReturn(List.of(mockProduct()));
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getAllProducts_withInvalidStore_shouldReturnEmptyList() throws Exception {
        when(productService.getAllProducts("Unknown")).thenReturn(List.of());
        mockMvc.perform(get("/products?store=Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // --- /products/{productId}/price-history ---

    @Test
    void getPriceHistoryById_shouldReturnHistory() throws Exception {
        when(productService.getPriceHistoryById("P001", null)).thenReturn(List.of(mockProduct()));
        mockMvc.perform(get("/products/P001/price-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getPriceHistoryById_blank_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/products//price-history"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPriceHistoryById_notFound_shouldReturnEmpty() throws Exception {
        when(productService.getPriceHistoryById("INVALID", null)).thenReturn(List.of());
        mockMvc.perform(get("/products/INVALID/price-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // --- /products/name/price-history ---

    @Test
    void getPriceHistoryByName_shouldReturnResults() throws Exception {
        when(productService.getPriceHistoryByName("milk", null, null, null))
                .thenReturn(List.of(mockProduct()));
        mockMvc.perform(get("/products/name/price-history?name=milk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getPriceHistoryByName_withAllFilters() throws Exception {
        when(productService.getPriceHistoryByName("milk", "Lidl", "BrandA", "Dairy"))
                .thenReturn(List.of(mockProduct()));
        mockMvc.perform(get("/products/name/price-history?name=milk&store=Lidl&brand=BrandA&category=Dairy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getPriceHistoryByName_missingName_shouldReturn400() throws Exception {
        mockMvc.perform(get("/products/name/price-history"))
                .andExpect(status().isBadRequest());
    }

    // --- /products/{productId}/substitutes ---

    @Test
    void getSubstitutes_shouldReturnSubstitutes() throws Exception {
        when(productService.findSubstitutes("P001", 10.0)).thenReturn(List.of(mockProduct()));
        mockMvc.perform(get("/products/P001/substitutes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getSubstitutes_withCustomMargin_shouldWork() throws Exception {
        when(productService.findSubstitutes("P001", 5.0)).thenReturn(List.of(mockProduct()));
        mockMvc.perform(get("/products/P001/substitutes?margin=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getSubstitutes_blankProductId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/products//substitutes"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSubstitutes_invalidMargin_shouldStillDefault() throws Exception {
        when(productService.findSubstitutes("P001", 10.0)).thenReturn(List.of(mockProduct()));
        mockMvc.perform(get("/products/P001/substitutes?margin=abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSubstitutes_productNotFound_shouldReturnEmpty() throws Exception {
        when(productService.findSubstitutes("INVALID", 10.0)).thenReturn(List.of());
        mockMvc.perform(get("/products/INVALID/substitutes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}