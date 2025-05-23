package com.example.accesa.service;

import com.example.accesa.domain.Discount;
import com.example.accesa.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepo;

    /**
     * Retrieves all available discounts from the repository.
     *
     * @return a list of all Discount entities
     */
    public List<Discount> getAllDiscounts() {
        return discountRepo.findAll();
    }

    /**
     * Retrieves discounts that have started within the last 24 hours.
     *
     * @return a list of new Discount entities
     */
    public List<Discount> getNewDiscounts() {
        return discountRepo.findById_FromDateGreaterThanEqual(LocalDate.now().minusDays(1));
    }

    /**
     * Retrieves the top N discounts sorted by the highest percentage.
     *
     * @param top the maximum number of discounts to return
     * @return a list of top Discount entities with the highest percentages
     */
    public List<Discount> getBestDiscounts(int top) {
        return discountRepo.findAll().stream()
                .sorted(Comparator.comparing(Discount::getPercentage).reversed())
                .limit(top)
                .toList();
    }
}