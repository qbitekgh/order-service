package com.example.orderservice.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguracja RabbitMQ: exchange, kolejka i powiazanie (binding) miedzy nimi.
 *
 * Przeplyw: OrderService publikuje wiadomosc do "order.exchange"
 * z routing key "order.created" -> trafia do "order.created.queue"
 * -> OrderCreatedListener ja odbiera.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "order.exchange";
    public static final String QUEUE_NAME = "order.created.queue";
    public static final String ROUTING_KEY = "order.created";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(QUEUE_NAME, true); // durable = true, kolejka przetrwa restart brokera
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with(ROUTING_KEY);
    }

    /**
     * Konwerter JSON, dzieki ktoremu obiekty Java (np. OrderCreatedEvent)
     * sa automatycznie serializowane/deserializowane do/z JSON-a
     * zamiast domyslnej (mniej czytelnej) serializacji Javy.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
