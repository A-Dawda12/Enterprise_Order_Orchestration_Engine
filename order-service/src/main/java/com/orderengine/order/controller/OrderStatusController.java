package com.orderengine.order.controller;

import com.orderengine.order.api.dto.OrderResponse;
import com.orderengine.order.api.dto.StatusUpdateRequest;
import com.orderengine.order.api.mapper.OrderApiMapper;
import com.orderengine.order.domain.OrderEntity;
import com.orderengine.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/orders")
public class OrderStatusController {

    private final OrderService orderService;

    public OrderStatusController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PatchMapping("/{orderId}/status")
    public OrderResponse updateStatus(
            @PathVariable String orderId,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        log.info("updateStatus orderId={} status={}", orderId, request.status());
        OrderEntity updated = orderService.updateStatus(orderId, request.status(), request.reason());
        return OrderApiMapper.toResponse(updated);
    }
}
