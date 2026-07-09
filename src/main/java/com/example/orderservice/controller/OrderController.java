package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderRequestDto;
import com.example.orderservice.dto.OrderResponseDto;
import com.example.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST API do zarzadzania zamowieniami.
 *
 * POST   /orders      - tworzy nowe zamowienie
 * GET    /orders/{id} - zwraca zamowienie po ID
 * GET    /orders       - zwraca liste wszystkich zamowien
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto request) {
        OrderResponseDto created = orderService.createOrder(request);
        URI location = URI.create("/orders/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long id) {
        OrderResponseDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
