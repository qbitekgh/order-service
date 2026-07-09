package com.example.orderservice.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Rabat procentowy - 10% od sumy zamowienia.
 * Bean o nazwie "PERCENTAGE10" - to ten string jest wpisywany
 * w pole discountCode zamowienia.
 */
@Component("PERCENTAGE10")
public class PercentageDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal PERCENTAGE = new BigDecimal("0.10");

    @Override
    public BigDecimal calculateDiscount(BigDecimal subtotal) {
        return subtotal.multiply(PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
    }
}
