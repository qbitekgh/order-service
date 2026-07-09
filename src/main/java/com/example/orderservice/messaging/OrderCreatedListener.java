package com.example.orderservice.messaging;

import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Nasluchuje na kolejke "order.created.queue".
 * Po odebraniu eventu OrderCreated: loguje informacje i aktualizuje
 * status zamowienia w bazie na PROCESSED.
 *
 * Dzieki temu tworzenie zamowienia (POST /orders) nie czeka na te reakcje -
 * dzieje sie ona asynchronicznie, w tle.
 */
@Component
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    private final OrderRepository orderRepository;

    public OrderCreatedListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Odebrano event OrderCreated: {} -> wysylanie powiadomienia (symulacja)", event);

        orderRepository.findById(event.getOrderId()).ifPresentOrElse(order -> {
            order.setStatus(OrderStatus.PROCESSED);
            orderRepository.save(order);
            log.info("Zamowienie {} oznaczone jako PROCESSED", order.getId());
        }, () -> log.warn("Nie znaleziono zamowienia o ID {} podczas przetwarzania eventu", event.getOrderId()));
    }
}
