package com.market.order;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderEndpoint {

    private final OrderService orderService;

    @RabbitListener(queues = "${message.queue.err.order}")
    public void errOrder(DeliveryMessage deliveryMessage) {
        log.error("Order Error: {}", deliveryMessage.toString());
        orderService.rollbackOrder(deliveryMessage);
    }

    @GetMapping("/orders/{orders_id}")
    public ResponseEntity<Order> getOrder(@PathVariable("orders_id") UUID orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody OrderReqDto reqDto) {
        Order order = orderService.createOrder(reqDto);
        return ResponseEntity.ok(order);
    }

    @Data
    public static class OrderReqDto {
        private String userId;
        private Integer productId;
        private Integer productQuantity;
        private Integer payAmount;

        public Order toOrder() {
            return Order.builder()
                    .orderId(UUID.randomUUID())
                    .userId(userId)
                    .orderStatus("RECEIPT")
                    .build();
        }

        public DeliveryMessage toDeliveryMessage(UUID orderId) {
            return DeliveryMessage.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .productId(productId)
                    .productQuantity(productQuantity)
                    .payAmount(payAmount)
                    .build();
        }
    }
}
