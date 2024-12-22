package com.market.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final RabbitTemplate rabbitTemplate;
    private Map<UUID, Order> orderStore = new HashMap<>();

    @Value("${message.queue.product}")
    private String productQueue;

    public Order createOrder(OrderEndpoint.OrderReqDto reqDto) {
        Order order = reqDto.toOrder();

        orderStore.put(order.getOrderId(), order);
        DeliveryMessage deliveryMessage = reqDto.toDeliveryMessage(order.getOrderId());

        rabbitTemplate.convertAndSend(productQueue, deliveryMessage);
        return order;
    }

    public Order getOrder(UUID orderId) {
        return orderStore.get(orderId);
    }

    public void rollbackOrder(DeliveryMessage deliveryMessage) {
        Order order = orderStore.get(deliveryMessage.getOrderId());
        order.cancelOrder(deliveryMessage.getErrorType());
    }
}
