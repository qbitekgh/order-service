package com.example.orderservice.repository;

import com.example.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repozytorium dajace gotowe operacje CRUD na encji Order
 * (save, findById, findAll, deleteById, itd.) bez pisania SQL-a.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
