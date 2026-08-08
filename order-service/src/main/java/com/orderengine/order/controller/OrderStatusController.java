package com.orderengine.order.controller;

import com.orderengine.order.api.dto.OrderResponse;
import com.orderengine.order.api.dto.StatusUpdateRequest;
import com.orderengine.order.api.mapper.OrderApiMapper;
import com.orderengine.order.domain.OrderEntity;
import com.orderengine.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/orders")
@Tag(name = "Orders", description = "Create, get and list orders")
public class OrderStatusController {

    private final OrderService orderService;

    public OrderStatusController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Updates the status of an order, with optional reason for the change")
    public OrderResponse updateStatus(
            @PathVariable String orderId,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        log.info("updateStatus orderId={} status={}", orderId, request.status());
        OrderEntity updated = orderService.updateStatus(orderId, request.status(), request.reason());
        return OrderApiMapper.toResponse(updated);
    }
}
