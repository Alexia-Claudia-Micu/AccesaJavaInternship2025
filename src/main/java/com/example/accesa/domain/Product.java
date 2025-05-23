package com.example.accesa.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @EmbeddedId
    private ProductId id;

    private String name;
    private String category;
    private String brand;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal price;
    private String currency;

    public BigDecimal getUnitPrice() {
        return quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0
                ? price.divide(quantity, 4, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
    }
}