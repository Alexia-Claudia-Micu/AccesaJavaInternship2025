package com.example.accesa.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountTest {

    @Test
    void isActive_shouldReturnTrueIfTodayWithinRange() {
        Discount discount = new Discount();
        discount.setId(new DiscountId("P001", "Lidl", LocalDate.now().minusDays(1)));
        discount.setToDate(LocalDate.now().plusDays(1));

        assertThat(discount.isActive(LocalDate.now())).isTrue();
    }

    @Test
    void isActive_shouldReturnFalseIfTodayBeforeFromDate() {
        Discount discount = new Discount();
        discount.setId(new DiscountId("P001", "Lidl", LocalDate.now().plusDays(1)));
        discount.setToDate(LocalDate.now().plusDays(3));

        assertThat(discount.isActive(LocalDate.now())).isFalse();
    }

    @Test
    void isActive_shouldReturnFalseIfTodayAfterToDate() {
        Discount discount = new Discount();
        discount.setId(new DiscountId("P001", "Lidl", LocalDate.now().minusDays(5)));
        discount.setToDate(LocalDate.now().minusDays(1));

        assertThat(discount.isActive(LocalDate.now())).isFalse();
    }
}