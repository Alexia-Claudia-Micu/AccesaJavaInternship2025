package com.example.accesa.service;

import com.example.accesa.domain.PriceAlert;
import com.example.accesa.domain.Product;
import com.example.accesa.dto.PriceAlertRequest;
import com.example.accesa.repository.PriceAlertRepository;
import com.example.accesa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final PriceAlertRepository alertRepo;
    private final ProductRepository productRepo;

    /**
     * Creates a new PriceAlert entity from the provided request data.
     * Sets the current date as the creation time and persists the alert.
     *
     * @param request the incoming request data for the alert
     * @return the saved PriceAlert entity
     */
    public PriceAlert createAlert(PriceAlertRequest request) {
        PriceAlert alert = new PriceAlert();
        alert.setProductId(request.productId());
        alert.setStoreName(request.storeName());
        alert.setTargetPrice(request.targetPrice());
        alert.setCreatedAt(LocalDate.now());
        return alertRepo.save(alert);
    }

    /**
     * Retrieves all alerts that are considered "active", meaning:
     * - The latest price of the product is less than or equal to the target price in the alert.
     *
     * @return a list of active PriceAlerts
     */
    public List<PriceAlert> getActiveAlerts() {
        return alertRepo.findAll().stream()
                .filter(alert -> productRepo.findById_ProductIdOrderById_DateAsc(alert.getProductId()).stream()
                        .max(Comparator.comparing(a -> a.getId().getDate()))
                        .map(Product::getPrice)
                        .orElse(BigDecimal.valueOf(Double.MAX_VALUE))
                        .compareTo(alert.getTargetPrice()) <= 0)
                .toList();
    }
}