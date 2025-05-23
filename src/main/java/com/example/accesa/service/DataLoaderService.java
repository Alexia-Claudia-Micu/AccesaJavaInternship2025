package com.example.accesa.service;

import com.example.accesa.domain.Product;
import com.example.accesa.domain.Discount;
import com.example.accesa.domain.DiscountId;
import com.example.accesa.domain.ProductId;
import com.example.accesa.repository.ProductRepository;
import com.example.accesa.repository.DiscountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DataLoaderService {
    private final ProductRepository productRepo;
    private final DiscountRepository discountRepo;

    /**
     * Initializes and loads all product and discount data from CSV files located in the /data directory.
     * This runs automatically after the Spring context is initialized.
     */
    @PostConstruct
    public void loadData() {
        String[] stores = {"Lidl", "Kaufland", "Profi"};

        for (String store : stores) {
            try {
                loadStoreData(store);
            } catch (Exception e) {
                System.err.println("Failed to load data for store: " + store);
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads all product or discount CSV files for a specific store.
     *
     * @param store the store name to load data for (e.g., "Lidl")
     * @throws IOException if an error occurs reading the files
     */
    private void loadStoreData(String store) throws IOException {
        String storeLower = store.toLowerCase();
        File folder = new File(Objects.requireNonNull(getClass().getResource("/data")).getFile());
        File[] files = folder.listFiles();

        if (files == null) return;

        for (File file : files) {
            String fileName = file.getName();
            if (!fileName.startsWith(storeLower)) continue;

            if (fileName.contains("discounts")) {
                loadDiscounts(fileName, store);
            } else {
                loadProducts(fileName, store);
            }
        }
    }

    /**
     * Parses a CSV file and loads product records into the database.
     * Each product is assigned a unique ID that includes product ID, store, and date.
     *
     * @param filename the name of the CSV file
     * @param store    the store from which the product data originated
     */
    private void loadProducts(String filename, String store) {
        try (InputStream is = getClass().getResourceAsStream("/data/" + filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)))) {

            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 8) continue;

                try {
                    String[] tokens = filename.replace(".csv", "").split("_");
                    String dateStr = tokens[tokens.length - 1];
                    LocalDate date = LocalDate.parse(dateStr);

                    Product p = new Product();
                    p.setId(new ProductId(parts[0], store, date));
                    p.setName(parts[1]);
                    p.setCategory(parts[2]);
                    p.setBrand(parts[3]);
                    p.setQuantity(new BigDecimal(parts[4]));
                    p.setUnit(parts[5]);
                    p.setPrice(new BigDecimal(parts[6]));
                    p.setCurrency(parts[7]);

                    productRepo.save(p);
                } catch (Exception e) {
                    System.err.println("Skipping invalid product line in " + filename + ": " + line);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load products from file: " + filename);
        }
    }

    /**
     * Parses a CSV file and loads discount records into the database.
     * Each discount is associated with a product and store, and includes a validity date range.
     *
     * @param filename the name of the discount CSV file
     * @param store    the store associated with the discounts
     */
    private void loadDiscounts(String filename, String store) {
        try (InputStream is = getClass().getResourceAsStream("/data/" + filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)))) {

            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 9) continue;

                try {
                    DiscountId id = new DiscountId();
                    id.setProductId(parts[0]);
                    id.setStoreName(store);
                    id.setFromDate(LocalDate.parse(parts[6]));

                    Discount d = new Discount();
                    d.setId(id);
                    d.setName(parts[1]);
                    d.setBrand(parts[2]);
                    d.setQuantity(new BigDecimal(parts[3]));
                    d.setUnit(parts[4]);
                    d.setCategory(parts[5]);
                    d.setToDate(LocalDate.parse(parts[7]));
                    d.setPercentage(new BigDecimal(parts[8]));

                    discountRepo.save(d);
                } catch (Exception e) {
                    System.err.println("Skipping invalid discount line in " + filename + ": " + line);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load discounts from file: " + filename);
        }
    }
}