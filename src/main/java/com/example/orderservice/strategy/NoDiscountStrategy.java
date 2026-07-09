package com.example.orderservice.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Domyslna strategia - brak rabatu. Uzywana, gdy zamowienie
 * nie ma podanego kodu rabatowego albo kod jest nieznany.
 */
@Component("NONE")
public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal calculateDiscount(BigDecimal subtotal) {
        return BigDecimal.ZERO;
    }
}
