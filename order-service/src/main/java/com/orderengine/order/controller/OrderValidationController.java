package com.orderengine.order.controller;

import com.orderengine.order.api.dto.ValidationResultResponse;
import com.orderengine.order.service.OrderService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/orders")
public class OrderValidationController {

    private final OrderService orderService;

    public OrderValidationController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{orderId}/validate")
    public ValidationResultResponse validateOrder(@PathVariable String orderId) {
        OrderService.ValidationResult result = orderService.validateOrder(orderId);
        return new ValidationResultResponse(
                result.valid(),
                result.validItems(),
                result.validAddress(),
                result.errors()
        );
    }
}
