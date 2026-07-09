package com.example.orderservice.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountStrategyTest {

    @Test
    void noDiscount_shouldReturnZero() {
        DiscountStrategy strategy = new NoDiscountStrategy();
        assertEquals(BigDecimal.ZERO, strategy.calculateDiscount(new BigDecimal("100.00")));
    }

    @Test
    void percentageDiscount_shouldReturnTenPercent() {
        DiscountStrategy strategy = new PercentageDiscountStrategy();
        assertEquals(new BigDecimal("10.00"), strategy.calculateDiscount(new BigDecimal("100.00")));
    }

    @Test
    void fixedDiscount_shouldNotExceedSubtotal() {
        DiscountStrategy strategy = new FixedAmountDiscountStrategy();
        assertEquals(new BigDecimal("10.00"), strategy.calculateDiscount(new BigDecimal("10.00")));
        assertEquals(new BigDecimal("20.00"), strategy.calculateDiscount(new BigDecimal("100.00")));
    }
}
