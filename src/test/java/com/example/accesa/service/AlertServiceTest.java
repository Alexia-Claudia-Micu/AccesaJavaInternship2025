package com.example.accesa.service;

import com.example.accesa.domain.PriceAlert;
import com.example.accesa.domain.Product;
import com.example.accesa.domain.ProductId;
import com.example.accesa.dto.PriceAlertRequest;
import com.example.accesa.repository.PriceAlertRepository;
import com.example.accesa.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AlertServiceTest {

    private PriceAlertRepository alertRepo;
    private ProductRepository productRepo;
    private AlertService alertService;

    @BeforeEach
    void setup() {
        alertRepo = mock(PriceAlertRepository.class);
        productRepo = mock(ProductRepository.class);
        alertService = new AlertService(alertRepo, productRepo);
    }

    @Test
    void createAlert_shouldSaveWithCurrentDate() {
        PriceAlertRequest request = new PriceAlertRequest("P001", "Lidl", new BigDecimal("10.00"));

        PriceAlert saved = new PriceAlert();
        saved.setId(1L);
        saved.setProductId("P001");
        saved.setStoreName("Lidl");
        saved.setTargetPrice(new BigDecimal("10.00"));
        saved.setCreatedAt(LocalDate.now());

        when(alertRepo.save(any(PriceAlert.class))).thenReturn(saved);

        PriceAlert result = alertService.createAlert(request);

        assertThat(result.getProductId()).isEqualTo("P001");
        assertThat(result.getStoreName()).isEqualTo("Lidl");
        assertThat(result.getTargetPrice()).isEqualByComparingTo("10.00");
        assertThat(result.getCreatedAt()).isEqualTo(LocalDate.now());

        verify(alertRepo).save(any(PriceAlert.class));
    }

    @Test
    void getActiveAlerts_shouldReturnOnlyTriggeredAlerts() {
        PriceAlert alert = new PriceAlert();
        alert.setId(1L);
        alert.setProductId("P001");
        alert.setStoreName("Lidl");
        alert.setTargetPrice(new BigDecimal("5.00"));

        Product product = new Product();
        product.setId(new ProductId("P001", "Lidl", LocalDate.now()));
        product.setPrice(new BigDecimal("4.99"));

        when(alertRepo.findAll()).thenReturn(List.of(alert));
        when(productRepo.findById_ProductIdOrderById_DateAsc("P001")).thenReturn(List.of(product));

        List<PriceAlert> active = alertService.getActiveAlerts();

        assertThat(active).containsExactly(alert);
    }

    @Test
    void getActiveAlerts_shouldReturnEmptyIfPriceIsTooHigh() {
        PriceAlert alert = new PriceAlert();
        alert.setProductId("P002");
        alert.setTargetPrice(new BigDecimal("5.00"));

        Product product = new Product();
        product.setId(new ProductId("P002", "Profi", LocalDate.now()));
        product.setPrice(new BigDecimal("5.10"));

        when(alertRepo.findAll()).thenReturn(List.of(alert));
        when(productRepo.findById_ProductIdOrderById_DateAsc("P002")).thenReturn(List.of(product));

        List<PriceAlert> active = alertService.getActiveAlerts();

        assertThat(active).isEmpty();
    }

    @Test
    void getActiveAlerts_shouldIgnoreIfNoPriceData() {
        PriceAlert alert = new PriceAlert();
        alert.setProductId("P003");
        alert.setTargetPrice(new BigDecimal("9.99"));

        when(alertRepo.findAll()).thenReturn(List.of(alert));
        when(productRepo.findById_ProductIdOrderById_DateAsc("P003")).thenReturn(List.of());

        List<PriceAlert> active = alertService.getActiveAlerts();

        assertThat(active).isEmpty();
    }
}