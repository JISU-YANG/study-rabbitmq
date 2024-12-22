package com.market.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${message.queue.err.product}")
    private String productErrorQueue;

    public void createPayment(DeliveryMessage deliveryMessage) {
        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .userId(deliveryMessage.getUserId())
                .payAmount(deliveryMessage.getPayAmount())
                .payStatus("SUCCESS")
                .build();
        Integer payAmount = deliveryMessage.getPayAmount();
        if (payAmount >= 10000){
            deliveryMessage.setErrorType("PAYMENT_LIMIT_EXCEEDED");
            this.rollbackPayment(deliveryMessage);
        }
    }

    private void rollbackPayment(DeliveryMessage deliveryMessage) {
        log.info("Rollback payment {}", deliveryMessage.getPayAmount());
        rabbitTemplate.convertAndSend(productErrorQueue, deliveryMessage);
    }
}
