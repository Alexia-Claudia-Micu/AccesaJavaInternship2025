package com.example.accesa.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BasketRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation() {
        BasketRequest dto = new BasketRequest(List.of("P001", "P002"));
        Set<ConstraintViolation<BasketRequest>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenListIsEmpty() {
        BasketRequest dto = new BasketRequest(List.of());
        Set<ConstraintViolation<BasketRequest>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("cannot be empty"));
    }

    @Test
    void shouldFailWhenAnyProductIdIsBlank() {
        BasketRequest dto = new BasketRequest(List.of("P001", ""));
        Set<ConstraintViolation<BasketRequest>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getMessage().contains("must not be blank"));
    }
}