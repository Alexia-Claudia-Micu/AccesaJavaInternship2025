package com.example.accesa.service;

import com.example.accesa.domain.Product;
import com.example.accesa.domain.ProductId;
import com.example.accesa.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepo;
    private ProductService productService;

    @BeforeEach
    void setup() {
        productRepo = mock(ProductRepository.class);
        productService = new ProductService(productRepo);
    }

    private Product createProduct(String id, String store, String brand, String category, BigDecimal unitPrice, LocalDate date) {
        Product product = new Product();
        ProductId productId = new ProductId(id, store, date);
        product.setId(productId);
        product.setBrand(brand);
        product.setCategory(category);
        product.setName("Sample Product");
        return product;
    }

    @Test
    void getAllProducts_shouldReturnAll() {
        List<Product> products = List.of(createProduct("P001", "Lidl", "BrandA", "Dairy", BigDecimal.TEN, LocalDate.now()));
        when(productRepo.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts(null);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllProducts_shouldFilterByStore() {
        List<Product> products = List.of(createProduct("P001", "Lidl", "BrandA", "Dairy", BigDecimal.TEN, LocalDate.now()));
        when(productRepo.findById_StoreName("Lidl")).thenReturn(products);

        List<Product> result = productService.getAllProducts("Lidl");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId().getStoreName()).isEqualTo("Lidl");
    }

    @Test
    void getPriceHistoryById_shouldReturnFilteredByStore() {
        List<Product> allHistory = List.of(
                createProduct("P001", "Lidl", "BrandA", "Dairy", BigDecimal.TEN, LocalDate.now()),
                createProduct("P001", "Kaufland", "BrandA", "Dairy", BigDecimal.TEN, LocalDate.now())
        );
        when(productRepo.findById_ProductIdOrderById_DateAsc("P001")).thenReturn(allHistory);

        List<Product> result = productService.getPriceHistoryById("P001", "Lidl");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId().getStoreName()).isEqualTo("Lidl");
    }

    @Test
    void getPriceHistoryByName_shouldReturnFilteredList() {
        Product p = createProduct("P001", "Lidl", "BrandA", "Dairy", BigDecimal.TEN, LocalDate.now());
        p.setName("Fresh Milk");
        when(productRepo.findAll()).thenReturn(List.of(p));

        List<Product> result = productService.getPriceHistoryByName("milk", "Lidl", "BrandA", "Dairy");

        assertThat(result).hasSize(1);
    }

    @Test
    void findSubstitutes_shouldReturnAllMatchingVariantsSortedByUnitPrice() {
        Product reference = createProduct("P001", "Lidl", "BrandA", "Dairy", new BigDecimal("1"), LocalDate.now());
        reference.setPrice(new BigDecimal("1.99"));
        reference.setQuantity(new BigDecimal("1"));

        Product variant1 = createProduct("P001", "Kaufland", "BrandA", "Dairy", new BigDecimal("1"), LocalDate.now().minusDays(1));
        variant1.setPrice(new BigDecimal("1.89"));
        variant1.setQuantity(new BigDecimal("1"));

        Product variant2 = createProduct("P001", "Profi", "BrandA", "Dairy", new BigDecimal("1"), LocalDate.now().minusDays(2));
        variant2.setPrice(new BigDecimal("2.09"));
        variant2.setQuantity(new BigDecimal("1"));

        when(productRepo.findById_ProductIdOrderById_DateAsc("P001")).thenReturn(List.of(reference));
        when(productRepo.findAll()).thenReturn(List.of(reference, variant1, variant2));

        List<Product> result = productService.findSubstitutes("P001", 0.0);

        assertThat(result).hasSize(3);
        assertThat(result).isSortedAccordingTo(Comparator.comparing(Product::getUnitPrice));
        assertThat(result.get(0).getPrice()).isEqualByComparingTo("1.89");
    }

    @Test
    void findSubstitutes_whenNoHistory_shouldReturnEmpty() {
        when(productRepo.findById_ProductIdOrderById_DateAsc("P001")).thenReturn(List.of());

        List<Product> result = productService.findSubstitutes("P001", 10.0);

        assertThat(result).isEmpty();
    }
}