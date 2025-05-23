package com.example.accesa.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PriceAlertTest {

    @Test
    void isTriggeredBy_shouldReturnTrueWhenPriceIsBelowOrEqualTarget() {
        Product product = new Product();
        product.setId(new ProductId("P001", "Lidl", LocalDate.now()));
        product.setPrice(new BigDecimal("4.99"));

        PriceAlert alert = new PriceAlert(1L, "P001", "Lidl", new BigDecimal("5.00"), LocalDate.now());

        assertThat(alert.isTriggeredBy(product)).isTrue();
    }

    @Test
    void isTriggeredBy_shouldReturnFalseWhenProductIdMismatch() {
        Product product = new Product();
        product.setId(new ProductId("P002", "Lidl", LocalDate.now()));
        product.setPrice(new BigDecimal("4.00"));

        PriceAlert alert = new PriceAlert(1L, "P001", "Lidl", new BigDecimal("5.00"), LocalDate.now());

        assertThat(alert.isTriggeredBy(product)).isFalse();
    }

    @Test
    void isTriggeredBy_shouldReturnFalseWhenStoreMismatch() {
        Product product = new Product();
        product.setId(new ProductId("P001", "Profi", LocalDate.now()));
        product.setPrice(new BigDecimal("4.00"));

        PriceAlert alert = new PriceAlert(1L, "P001", "Lidl", new BigDecimal("5.00"), LocalDate.now());

        assertThat(alert.isTriggeredBy(product)).isFalse();
    }

    @Test
    void isTriggeredBy_shouldReturnFalseWhenPriceAboveTarget() {
        Product product = new Product();
        product.setId(new ProductId("P001", "Lidl", LocalDate.now()));
        product.setPrice(new BigDecimal("5.01"));

        PriceAlert alert = new PriceAlert(1L, "P001", "Lidl", new BigDecimal("5.00"), LocalDate.now());

        assertThat(alert.isTriggeredBy(product)).isFalse();
    }
}