package com.example.accesa.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BasketRequest(
        @NotEmpty(message = "Product list cannot be empty")
        List<@NotBlank String> productIds
) {}