package com.example.orderservice.exception;

/**
 * Rzucany, gdy zamowienie o podanym ID nie istnieje w bazie.
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super("Zamowienie o ID " + orderId + " nie zostalo znalezione");
    }
}
