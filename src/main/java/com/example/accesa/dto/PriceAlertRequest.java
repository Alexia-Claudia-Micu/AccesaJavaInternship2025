package com.example.accesa.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PriceAlertRequest(
        @NotNull(message = "productId is required") String productId,
        @NotNull(message = "storeName is required") String storeName,
        @NotNull(message = "targetPrice is required")
        @Positive(message = "targetPrice must be a positive number") BigDecimal targetPrice
) {}