package com.orderengine.order.controller;

import com.orderengine.order.api.dto.CheckResultResponse;
import com.orderengine.order.api.dto.ValidationResultResponse;
import com.orderengine.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/orders")
@Tag(name = "Orders", description = "Create, get and list orders")
public class OrderValidationController {

    private final OrderService orderService;

    public OrderValidationController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{orderId}/validate")
    @Operation(summary = "Validate order", description = "Validates items and shipping address, does not change status")
    public ValidationResultResponse validateOrder(@PathVariable String orderId) {
        OrderService.ValidationResult result = orderService.validateOrder(orderId);
        return new ValidationResultResponse(
                result.valid(),
                result.validItems(),
                result.validAddress(),
                result.errors()
        );
    }

    @PostMapping("/{orderId}/validate/items")
    @Operation(summary = "ValidateOrder Items", description = "Validates Item only ,does not change status")
    public CheckResultResponse validateOrderItems(@PathVariable String orderId) {
        OrderService.CheckResult result = orderService.validateOrderItems(orderId);
        return new CheckResultResponse(
                result.valid(),
                result.errors()
        );
    }

    @PostMapping("/{orderId}/validate/address")
    @Operation(summary = "ValidateOrder Address", description = "Validates Address only ,does not change status")
    public CheckResultResponse validateOrderAddress(@PathVariable String orderId) {
        OrderService.CheckResult result = orderService.validateShippingAddress(orderId);
        return new CheckResultResponse(
                result.valid(),
                result.errors()
        );
    }
}
