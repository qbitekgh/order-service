package com.example.orderservice.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Rabat kwotowy - stale 20 (w walucie zamowienia), ale nie wiecej
 * niz wartosc calego zamowienia (rabat nie moze zejsc ponizej zera).
 * Bean o nazwie "FIXED20".
 */
@Component("FIXED20")
public class FixedAmountDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal FIXED_AMOUNT = new BigDecimal("20.00");

    @Override
    public BigDecimal calculateDiscount(BigDecimal subtotal) {
        return FIXED_AMOUNT.min(subtotal);
    }
}
