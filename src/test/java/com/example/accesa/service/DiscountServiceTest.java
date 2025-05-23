package com.example.accesa.service;

import com.example.accesa.domain.Discount;
import com.example.accesa.domain.DiscountId;
import com.example.accesa.repository.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DiscountServiceTest {

    private DiscountRepository discountRepo;
    private DiscountService discountService;

    @BeforeEach
    void setup() {
        discountRepo = mock(DiscountRepository.class);
        discountService = new DiscountService(discountRepo);
    }

    private Discount createDiscount(String id, double percentage, LocalDate fromDate) {
        Discount d = new Discount();
        DiscountId discountId = new DiscountId();
        discountId.setProductId(id);
        discountId.setStoreName("TestStore");
        discountId.setFromDate(fromDate);
        d.setId(discountId);
        d.setPercentage(BigDecimal.valueOf(percentage));
        return d;
    }

    @Test
    void getAllDiscounts_shouldReturnAll() {
        List<Discount> mockDiscounts = List.of(
                createDiscount("P001", 10, LocalDate.now().minusDays(2)),
                createDiscount("P002", 15, LocalDate.now())
        );
        when(discountRepo.findAll()).thenReturn(mockDiscounts);

        List<Discount> result = discountService.getAllDiscounts();

        assertThat(result).hasSize(2);
    }

    @Test
    void getNewDiscounts_shouldReturnOnlyRecentOnes() {
        List<Discount> recent = List.of(
                createDiscount("P001", 10, LocalDate.now()),
                createDiscount("P002", 5, LocalDate.now())
        );
        when(discountRepo.findById_FromDateGreaterThanEqual(any(LocalDate.class))).thenReturn(recent);

        List<Discount> result = discountService.getNewDiscounts();

        assertThat(result).allMatch(d -> !d.getId().getFromDate().isBefore(LocalDate.now().minusDays(1)));
    }

    @Test
    void getBestDiscounts_shouldReturnSortedTopN() {
        List<Discount> mockDiscounts = List.of(
                createDiscount("P001", 10, LocalDate.now()),
                createDiscount("P002", 20, LocalDate.now()),
                createDiscount("P003", 30, LocalDate.now())
        );
        when(discountRepo.findAll()).thenReturn(mockDiscounts);

        List<Discount> topTwo = discountService.getBestDiscounts(2);

        assertThat(topTwo).hasSize(2);
        assertThat(topTwo.get(0).getPercentage()).isEqualTo(BigDecimal.valueOf(30.0));
        assertThat(topTwo.get(1).getPercentage()).isEqualTo(BigDecimal.valueOf(20.0));
    }

    @Test
    void getBestDiscounts_whenEmpty_shouldReturnEmptyList() {
        when(discountRepo.findAll()).thenReturn(List.of());

        List<Discount> result = discountService.getBestDiscounts(5);

        assertThat(result).isEmpty();
    }
}