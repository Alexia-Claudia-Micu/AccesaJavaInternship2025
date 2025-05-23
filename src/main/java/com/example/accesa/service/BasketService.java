package com.example.accesa.service;

import com.example.accesa.domain.Product;
import com.example.accesa.dto.BasketRequest;
import com.example.accesa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasketService {
    private final ProductRepository productRepo;

    /**
     * Optimizes the shopping basket by calculating the total price for each store
     * and returning the store with the lowest total that contains all requested products.
     *
     * @param request a list of product IDs representing the user's shopping basket
     * @return a map containing the optimal store and the total price, or a message if no store has all products
     */
    public Map<String, ? extends Serializable> optimizeBasket(BasketRequest request) {
        List<String> productIds = request.productIds();

        // Group all products by store name for processing
        Map<String, List<Product>> grouped = productRepo.findAll().stream()
                .filter(p -> productIds.contains(p.getId().getProductId()))
                .collect(Collectors.groupingBy(p -> p.getId().getStoreName()));

        Map<String, BigDecimal> storeTotals = new HashMap<>();

        for (var entry : grouped.entrySet()) {
            String store = entry.getKey();

            // For each productId, select the most recent entry (latest date)
            List<Product> products = entry.getValue().stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.groupingBy(p -> p.getId().getProductId()),
                            m -> m.values().stream()
                                    .map(list -> list.stream()
                                            .max(Comparator.comparing(p -> p.getId().getDate()))
                                            .orElse(null))
                                    .filter(Objects::nonNull)
                                    .toList()
                    ));

            // Only consider stores that have all requested products
            if (products.size() == productIds.size()) {
                BigDecimal total = products.stream()
                        .map(Product::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                storeTotals.put(store, total);
            }
        }

        // Return the store with the lowest total or a fallback message
        return storeTotals.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(entry -> Map.of("store", entry.getKey(), "total", entry.getValue()))
                .orElse(Map.of("message", "No store has all requested products"));
    }

    /**
     * Splits the shopping basket across multiple stores to select the cheapest available price
     * for each product, regardless of store, and calculates the total optimized price.
     *
     * @param request a list of product IDs representing the user's shopping basket
     * @return a map containing the total optimized price and the store assignment for each product
     */
    public Map<String, Object> splitOptimizeBasket(BasketRequest request) {
        List<String> productIds = request.productIds();

        // Group products by unique (productId + storeName) pair and get the latest entry per group
        List<Product> latestProducts = productRepo.findAll().stream()
                .filter(p -> productIds.contains(p.getId().getProductId()))
                .collect(Collectors.groupingBy(p -> List.of(p.getId().getProductId(), p.getId().getStoreName())))
                .values().stream()
                .map(list -> list.stream()
                        .max(Comparator.comparing(p -> p.getId().getDate()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        // For each product, choose the cheapest available price across all stores
        Map<String, Product> cheapestForEach = latestProducts.stream()
                .collect(Collectors.toMap(
                        p -> p.getId().getProductId(),
                        Function.identity(),
                        BinaryOperator.minBy(Comparator.comparing(Product::getPrice))
                ));

        // Compute total price
        BigDecimal total = cheapestForEach.values().stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Format response as a list of products with store and price
        List<Map<String, Object>> items = cheapestForEach.values().stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("productId", p.getId().getProductId());
                    map.put("store", p.getId().getStoreName());
                    map.put("price", p.getPrice());
                    return map;
                })
                .toList();

        return Map.of("total", total, "items", items);
    }
}