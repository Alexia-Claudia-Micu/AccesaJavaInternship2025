package com.example.accesa.controller;

import com.example.accesa.domain.Discount;
import com.example.accesa.service.DiscountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiscountController.class)
class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscountService discountService;

    private Discount mockDiscount() {
        Discount discount = new Discount();
        discount.setName("Milk");
        discount.setBrand("BrandA");
        discount.setCategory("Dairy");
        discount.setPercentage(new BigDecimal("10.5"));
        discount.setToDate(LocalDate.now().plusDays(5));
        return discount;
    }

    // --- /discounts ---

    @Test
    void getAllDiscounts_shouldReturnListOfDiscounts() throws Exception {
        when(discountService.getAllDiscounts()).thenReturn(List.of(mockDiscount()));

        mockMvc.perform(get("/discounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    // --- /discounts/best ---

    @Test
    void getBestDiscounts_shouldReturnTopDiscountsWithDefaultLimit() throws Exception {
        when(discountService.getBestDiscounts(5)).thenReturn(List.of(mockDiscount()));

        mockMvc.perform(get("/discounts/best"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    // --- /discounts/best?top={N} ---

    @Test
    void getBestDiscounts_shouldReturnTopNDiscounts() throws Exception {
        when(discountService.getBestDiscounts(3)).thenReturn(List.of(mockDiscount(), mockDiscount(), mockDiscount()));

        mockMvc.perform(get("/discounts/best?top=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    // --- /discounts/new ---

    @Test
    void getNewDiscounts_shouldReturnRecentDiscounts() throws Exception {
        when(discountService.getNewDiscounts()).thenReturn(List.of(mockDiscount()));

        mockMvc.perform(get("/discounts/new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}