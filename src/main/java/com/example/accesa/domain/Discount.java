package com.example.accesa.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount {

    @EmbeddedId
    private DiscountId id;

    private String name;
    private String brand;
    private BigDecimal quantity;
    private String unit;
    private String category;
    private LocalDate toDate;
    private BigDecimal percentage;

    public boolean isActive(LocalDate today) {
        return !today.isBefore(id.getFromDate()) && !today.isAfter(toDate);
    }
}