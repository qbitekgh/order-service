package com.example.orderservice.messaging;

import java.io.Serializable;

/**
 * Event publikowany do RabbitMQ po utworzeniu nowego zamowienia.
 */
public class OrderCreatedEvent implements Serializable {

    private Long orderId;
    private String customerName;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Long orderId, String customerName) {
        this.orderId = orderId;
        this.customerName = customerName;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{orderId=" + orderId + ", customerName='" + customerName + "'}";
    }
}
