package com.example.orderservice.service;

import com.example.orderservice.dto.OrderItemRequestDto;
import com.example.orderservice.dto.OrderItemResponseDto;
import com.example.orderservice.dto.OrderRequestDto;
import com.example.orderservice.dto.OrderResponseDto;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.messaging.OrderCreatedEvent;
import com.example.orderservice.messaging.RabbitMQConfig;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderItem;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.strategy.DiscountStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Logika biznesowa zwiazana z zamowieniami:
 * - zapis zamowienia z wyliczeniem ceny koncowej (Strategy pattern dla rabatow)
 * - publikacja eventu OrderCreated do RabbitMQ po zapisie
 * - mapowanie miedzy encjami a DTO
 */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final Map<String, DiscountStrategy> discountStrategies;

    /**
     * Spring automatycznie wstrzykuje tu mape wszystkich beanow implementujacych
     * DiscountStrategy, gdzie kluczem jest nazwa beana (np. "NONE", "PERCENTAGE10").
     * Dodanie nowej strategii = nowa klasa @Component, bez zmiany tego serwisu.
     */
    public OrderService(OrderRepository orderRepository,
                        RabbitTemplate rabbitTemplate,
                        Map<String, DiscountStrategy> discountStrategies) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.discountStrategies = discountStrategies;
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setDiscountCode(request.getDiscountCode());

        for (OrderItemRequestDto itemDto : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setProductName(itemDto.getProductName());
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(itemDto.getPrice());
            order.addItem(item);
        }

        BigDecimal subtotal = order.calculateSubtotal();
        DiscountStrategy strategy = resolveStrategy(request.getDiscountCode());
        BigDecimal discount = strategy.calculateDiscount(subtotal);
        order.setTotalPrice(subtotal.subtract(discount).max(BigDecimal.ZERO));

        Order saved = orderRepository.save(order);

        publishOrderCreatedEvent(saved);

        return toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return toResponseDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    private DiscountStrategy resolveStrategy(String discountCode) {
        if (discountCode == null || !discountStrategies.containsKey(discountCode)) {
            return discountStrategies.get("NONE");
        }
        return discountStrategies.get(discountCode);
    }

    private void publishOrderCreatedEvent(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(order.getId(), order.getCustomerName());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
        log.info("Opublikowano event OrderCreated: {}", event);
    }

    private OrderResponseDto toResponseDto(Order order) {
        List<OrderItemResponseDto> items = order.getItems().stream()
                .map(item -> new OrderItemResponseDto(
                        item.getId(), item.getProductName(), item.getQuantity(), item.getPrice()))
                .toList();

        return new OrderResponseDto(
                order.getId(),
                order.getCustomerName(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getTotalPrice(),
                items
        );
    }
}