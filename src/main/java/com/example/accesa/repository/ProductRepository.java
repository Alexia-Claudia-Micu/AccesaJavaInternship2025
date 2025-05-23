package com.example.accesa.repository;

import com.example.accesa.domain.Product;
import com.example.accesa.domain.ProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, ProductId> {
    List<Product> findById_StoreName(String storeName);
    List<Product> findById_ProductIdOrderById_DateAsc(String productId);
    List<Product> findByCategoryAndBrandNot(String category, String excludedBrand);
}