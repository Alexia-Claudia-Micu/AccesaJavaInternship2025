package com.example.accesa.service;

import com.example.accesa.domain.Product;
import com.example.accesa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepo;

    /**
     * Retrieves all products, optionally filtered by store name.
     *
     * @param store the name of the store to filter by, or null to fetch all
     * @return a list of Product entities
     */
    public List<Product> getAllProducts(String store) {
        if (store == null) return productRepo.findAll();
        return productRepo.findById_StoreName(store);
    }

    /**
     * Retrieves the price history of a product by its ID,
     * optionally filtered by store.
     *
     * @param productId the ID of the product
     * @param store     the name of the store to filter by, or null
     * @return a list of Product records sorted by date
     */
    public List<Product> getPriceHistoryById(String productId, String store) {
        return productRepo.findById_ProductIdOrderById_DateAsc(productId).stream()
                .filter(p -> store == null || p.getId().getStoreName().equalsIgnoreCase(store))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the price history of a product by name,
     * optionally filtered by store, brand, and category.
     *
     * @param name     the product name to search
     * @param store    optional store name
     * @param brand    optional brand filter
     * @param category optional category filter
     * @return a filtered and sorted list of Product entities
     */
    public List<Product> getPriceHistoryByName(String name, String store, String brand, String category) {
        return productRepo.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> store == null || p.getId().getStoreName().equalsIgnoreCase(store))
                .filter(p -> brand == null || p.getBrand().equalsIgnoreCase(brand))
                .filter(p -> category == null || p.getCategory().equalsIgnoreCase(category))
                .sorted(Comparator.comparing(p -> p.getId().getDate()))
                .toList();
    }

    /**
     * Finds recommended substitute products based on similar category but different brand,
     * and within 10% of the reference unit price.
     *
     * @param productId the ID of the reference product
     * @return a list of substitute Product entities
     */
    public List<Product> findSubstitutes(String productId, double marginPercent) {
        List<Product> history = productRepo.findById_ProductIdOrderById_DateAsc(productId);
        if (history.isEmpty()) return List.of();

        Product reference = history.get(history.size() - 1);

        BigDecimal marginMultiplier = BigDecimal.valueOf(1 + (marginPercent / 100.0));

        return productRepo.findByCategoryAndBrandNot(reference.getCategory(), reference.getBrand()).stream()
                .filter(p -> p.getUnitPrice().compareTo(reference.getUnitPrice().multiply(marginMultiplier)) <= 0)
                .sorted(Comparator.comparing(Product::getUnitPrice))
                .limit(5)
                .toList();
    }
}