package com.example.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * DTO przychodzace z requestu POST /orders.
 * Oddzielone od encji Order, zeby nie wystawiac struktury bazy danych na zewnatrz.
 */
public class OrderRequestDto {

    @NotBlank(message = "Nazwa klienta nie moze byc pusta")
    private String customerName;

    @NotEmpty(message = "Zamowienie musi zawierac przynajmniej jedna pozycje")
    @Valid
    private List<OrderItemRequestDto> items;

    /**
     * Opcjonalny kod rabatu, np. "PERCENTAGE10", "FIXED20".
     * Brak wartosci = brak rabatu.
     */
    private String discountCode;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<OrderItemRequestDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDto> items) {
        this.items = items;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }
}
