package com.example.accesa.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PriceAlertRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation() {
        PriceAlertRequest dto = new PriceAlertRequest("P001", "Lidl", BigDecimal.valueOf(9.99));
        Set<ConstraintViolation<PriceAlertRequest>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenFieldsAreNull() {
        PriceAlertRequest dto = new PriceAlertRequest(null, null, null);
        Set<ConstraintViolation<PriceAlertRequest>> violations = validator.validate(dto);
        assertThat(violations).hasSize(3);
    }

    @Test
    void shouldFailWhenPriceIsNegative() {
        PriceAlertRequest dto = new PriceAlertRequest("P001", "Lidl", BigDecimal.valueOf(-1));
        Set<ConstraintViolation<PriceAlertRequest>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("must be a positive number"));
    }
}