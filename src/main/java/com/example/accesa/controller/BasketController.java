package com.example.accesa.controller;

import com.example.accesa.dto.ApiResponse;
import com.example.accesa.dto.BasketRequest;
import com.example.accesa.service.BasketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/basket")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    /**
     * Optimizes the basket for a single store that has all products at the lowest total price.
     */
    @PostMapping("/optimize")
    public ResponseEntity<ApiResponse<Map<String, ?>>> optimizeBasket(@Valid @RequestBody BasketRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, basketService.optimizeBasket(request), "Basket optimized"));
    }

    /**
     * Splits the basket across multiple stores to find the lowest price for each product.
     */
    @PostMapping("/split-optimize")
    public ResponseEntity<ApiResponse<Map<String, Object>>> splitOptimize(@Valid @RequestBody BasketRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, basketService.splitOptimizeBasket(request), "Basket split-optimized"));
    }
}