package com.example.accesa.repository;

import com.example.accesa.domain.Discount;
import com.example.accesa.domain.DiscountId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, DiscountId> {
    List<Discount> findById_FromDateGreaterThanEqual(LocalDate fromDate);
}