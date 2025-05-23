package com.example.accesa.controller;

import com.example.accesa.domain.Discount;
import com.example.accesa.dto.ApiResponse;
import com.example.accesa.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    /**
     * Retrieves all available discounts.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Discount>>> getAllDiscounts() {
        return ResponseEntity.ok(new ApiResponse<>(true, discountService.getAllDiscounts(), "All discounts retrieved"));
    }

    /**
     * Retrieves the top N best discounts by percentage.
     */
    @GetMapping("/best")
    public ResponseEntity<ApiResponse<List<Discount>>> getBestDiscounts(@RequestParam(defaultValue = "5") int top) {
        return ResponseEntity.ok(new ApiResponse<>(true, discountService.getBestDiscounts(top), "Top discounts retrieved"));
    }

    /**
     * Retrieves discounts added in the last 24 hours.
     */
    @GetMapping("/new")
    public ResponseEntity<ApiResponse<List<Discount>>> getNewDiscounts() {
        return ResponseEntity.ok(new ApiResponse<>(true, discountService.getNewDiscounts(), "New discounts from the last 24h"));
    }
}