package com.example.orderservice.dto;

import com.example.orderservice.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO wychodzace w odpowiedzi API - pelne zamowienie.
 */
public class OrderResponseDto {

    private Long id;
    private String customerName;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
    private List<OrderItemResponseDto> items;

    public OrderResponseDto() {
    }

    public OrderResponseDto(Long id, String customerName, OrderStatus status,
                             LocalDateTime createdAt, BigDecimal totalPrice,
                             List<OrderItemResponseDto> items) {
        this.id = id;
        this.customerName = customerName;
        this.status = status;
        this.createdAt = createdAt;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderItemResponseDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponseDto> items) {
        this.items = items;
    }
}
