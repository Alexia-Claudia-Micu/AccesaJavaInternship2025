package com.example.accesa.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "productId is required")
    private String productId;

    @NotNull(message = "storeName is required")
    private String storeName;

    @NotNull(message = "targetPrice is required")
    @Positive(message = "targetPrice must be a positive value")
    private BigDecimal targetPrice;

    private LocalDate createdAt;

    public boolean isTriggeredBy(Product product) {
        return product.getId().getProductId().equals(productId)
                && product.getId().getStoreName().equals(storeName)
                && product.getPrice().compareTo(targetPrice) <= 0;
    }
}