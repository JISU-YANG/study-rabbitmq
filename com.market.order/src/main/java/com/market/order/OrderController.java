package com.market.order;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders/{orders_id}")
    public String orders(@PathVariable("orders_id") String id) {
        orderService.createOrder(id);
        return "Order Complete";
    }
}
