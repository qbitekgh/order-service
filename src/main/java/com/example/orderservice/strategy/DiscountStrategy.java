package com.example.orderservice.strategy;

import java.math.BigDecimal;

/**
 * Strategia wyliczania rabatu (wzorzec projektowy Strategy).
 * Dzieki temu interfejsowi mozna dodac nowy sposob liczenia rabatu
 * (nowa klasa @Component) bez modyfikowania istniejacego kodu OrderService
 * - zgodnie z zasada Open/Closed z SOLID.
 */
public interface DiscountStrategy {

    /**
     * @param subtotal suma cen pozycji zamowienia przed rabatem
     * @return kwota rabatu do odjecia od subtotal
     */
    BigDecimal calculateDiscount(BigDecimal subtotal);
}
