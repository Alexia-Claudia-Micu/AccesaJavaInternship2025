package com.example.accesa.service;

import com.example.accesa.domain.Product;
import com.example.accesa.domain.ProductId;
import com.example.accesa.dto.BasketRequest;
import com.example.accesa.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BasketServiceTest {

    private ProductRepository productRepo;
    private BasketService basketService;

    @BeforeEach
    void setup() {
        productRepo = mock(ProductRepository.class);
        basketService = new BasketService(productRepo);
    }

    private Product createProduct(String productId, String store, LocalDate date, double price) {
        Product p = new Product();
        p.setId(new ProductId(productId, store, date));
        p.setPrice(BigDecimal.valueOf(price));
        return p;
    }

    @Test
    void optimizeBasket_shouldReturnCheapestStoreWithAllProducts() {
        List<Product> products = List.of(
                createProduct("P001", "Lidl", LocalDate.now(), 10.0),
                createProduct("P001", "Kaufland", LocalDate.now(), 9.0),
                createProduct("P002", "Lidl", LocalDate.now(), 5.0),
                createProduct("P002", "Kaufland", LocalDate.now(), 5.5)
        );
        when(productRepo.findAll()).thenReturn(products);

        BasketRequest request = new BasketRequest(List.of("P001", "P002"));
        Map<String, ?> result = basketService.optimizeBasket(request);

        assertThat(result.get("store")).isEqualTo("Kaufland");
        assertThat(result.get("total")).isEqualTo(BigDecimal.valueOf(14.5));
    }

    @Test
    void optimizeBasket_shouldReturnFallbackMessageIfNoStoreHasAllProducts() {
        List<Product> products = List.of(
                createProduct("P001", "Lidl", LocalDate.now(), 10.0),
                createProduct("P002", "Kaufland", LocalDate.now(), 5.0)
        );
        when(productRepo.findAll()).thenReturn(products);

        BasketRequest request = new BasketRequest(List.of("P001", "P002"));
        Map<String, ?> result = basketService.optimizeBasket(request);

        assertThat(result).containsKey("message");
        assertThat(result.get("message")).isEqualTo("No store has all requested products");
    }

    @Test
    void splitOptimizeBasket_shouldReturnCheapestCombinationAcrossStores() {
        List<Product> products = List.of(
                createProduct("P001", "Lidl", LocalDate.now(), 9.0),
                createProduct("P001", "Kaufland", LocalDate.now().minusDays(1), 10.0),
                createProduct("P002", "Profi", LocalDate.now(), 4.0)
        );
        when(productRepo.findAll()).thenReturn(products);

        BasketRequest request = new BasketRequest(List.of("P001", "P002"));
        Map<String, Object> result = basketService.splitOptimizeBasket(request);

        assertThat(result.get("total")).isEqualTo(BigDecimal.valueOf(13.0));
        List<Map<String, Object>> items = (List<Map<String, Object>>) result.get("items");
        assertThat(items).hasSize(2);
        assertThat(items).anyMatch(item -> item.get("store").equals("Lidl"));
        assertThat(items).anyMatch(item -> item.get("store").equals("Profi"));
    }

    @Test
    void splitOptimizeBasket_shouldHandleEmptyProductList() {
        when(productRepo.findAll()).thenReturn(Collections.emptyList());

        BasketRequest request = new BasketRequest(List.of("P001"));
        Map<String, Object> result = basketService.splitOptimizeBasket(request);

        assertThat(result.get("total")).isEqualTo(BigDecimal.ZERO);
        assertThat((List<?>) result.get("items")).isEmpty();
    }
}