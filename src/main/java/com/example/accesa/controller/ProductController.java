package com.example.accesa.controller;

import com.example.accesa.domain.Product;
import com.example.accesa.dto.ApiResponse;
import com.example.accesa.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Retrieves all products, optionally filtered by a store name.
     *
     * @param store the optional store name to filter products
     * @return a list of products wrapped in an API response
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts(@RequestParam(required = false) String store) {
        return ResponseEntity.ok(new ApiResponse<>(true, productService.getAllProducts(store), "Products retrieved"));
    }

    /**
     * Retrieves the price history of a product by its productId.
     *
     * @param productId the unique ID of the product
     * @param store     the optional store name to filter price history
     * @return a list of product price history records wrapped in an API response
     */
    @GetMapping("/{productId}/price-history")
    public ResponseEntity<ApiResponse<List<Product>>> getPriceHistoryById(
            @PathVariable String productId,
            @RequestParam(required = false) String store
    ) {
        if (productId == null || productId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "productId path variable is required and cannot be blank"));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, productService.getPriceHistoryById(productId, store), "Price history by ID"));
    }

    /**
     * Retrieves the price history of a product by partial name match and optional filters.
     *
     * @param name     the partial or full name of the product
     * @param store    the optional store name
     * @param brand    the optional brand
     * @param category the optional category
     * @return a filtered list of product price records wrapped in an API response
     */
    @GetMapping("/name/price-history")
    public ResponseEntity<ApiResponse<List<Product>>> getPriceHistoryByName(
            @RequestParam String name,
            @RequestParam(required = false) String store,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(new ApiResponse<>(true, productService.getPriceHistoryByName(name, store, brand, category), "Price history by name"));
    }

    /**
     * Retrieves substitute products within the same category but different brand,
     * sorted by unit price and within 10% margin of the original.
     *
     * @param productId the ID of the product to find substitutes for
     * @return a list of substitute products wrapped in an API response
     */
    @GetMapping("/{productId}/substitutes")
    public ResponseEntity<ApiResponse<List<Product>>> getSubstitutes(
            @PathVariable String productId,
            @RequestParam(required = false, defaultValue = "10") double margin
    ) {
        if (productId == null || productId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "productId path variable is required and cannot be blank"));
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, productService.findSubstitutes(productId, margin), "Substitutes retrieved")
        );
    }

}