package com.example.accesa.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void getUnitPrice_shouldDividePriceByQuantityCorrectly() {
        Product p = new Product();
        p.setQuantity(new BigDecimal("2.0"));
        p.setPrice(new BigDecimal("10.0"));
        assertThat(p.getUnitPrice()).isEqualByComparingTo("5.0000");
    }

    @Test
    void getUnitPrice_shouldReturnZeroWhenQuantityIsZero() {
        Product p = new Product();
        p.setQuantity(BigDecimal.ZERO);
        p.setPrice(new BigDecimal("10.0"));
        assertThat(p.getUnitPrice()).isEqualByComparingTo("0.0000");
    }

    @Test
    void getUnitPrice_shouldReturnZeroWhenQuantityIsNull() {
        Product p = new Product();
        p.setQuantity(null);
        p.setPrice(new BigDecimal("10.0"));
        assertThat(p.getUnitPrice()).isEqualByComparingTo("0.0000");
    }
}