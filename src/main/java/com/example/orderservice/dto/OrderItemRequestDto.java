package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO przychodzace z requestu - pojedyncza pozycja zamowienia.
 */
public class OrderItemRequestDto {

    @NotBlank(message = "Nazwa produktu nie moze byc pusta")
    private String productName;

    @NotNull(message = "Ilosc jest wymagana")
    @Positive(message = "Ilosc musi byc wieksza od zera")
    private Integer quantity;

    @NotNull(message = "Cena jest wymagana")
    @Positive(message = "Cena musi byc wieksza od zera")
    private BigDecimal price;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
