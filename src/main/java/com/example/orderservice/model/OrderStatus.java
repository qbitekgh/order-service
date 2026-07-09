package com.example.orderservice.model;

/**
 * Mozliwe statusy zamowienia w cyklu zycia.
 */
public enum OrderStatus {
    NEW,        // zamowienie zostalo utworzone
    PROCESSED,  // zamowienie zostalo obsluzone (po odebraniu eventu z RabbitMQ)
    CANCELLED   // zamowienie anulowane
}
