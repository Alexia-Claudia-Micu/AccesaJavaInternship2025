package com.example.accesa.controller;

import com.example.accesa.domain.PriceAlert;
import com.example.accesa.dto.ApiResponse;
import com.example.accesa.dto.PriceAlertRequest;
import com.example.accesa.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    /**
     * Creates a new price alert for a given product.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PriceAlert>> createAlert(@Valid @RequestBody PriceAlertRequest request) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, alertService.createAlert(request), "Alert created")
        );
    }

    /**
     * Retrieves all active alerts where the current product price is less than or equal to the target.
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PriceAlert>>> getActiveAlerts() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, alertService.getActiveAlerts(), "Active alerts retrieved")
        );
    }
}