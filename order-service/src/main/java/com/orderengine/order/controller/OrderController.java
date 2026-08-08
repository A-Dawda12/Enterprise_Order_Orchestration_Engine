package com.orderengine.order.controller;

import com.orderengine.order.api.dto.CreateOrderRequest;
import com.orderengine.order.api.dto.OrderResponse;
import com.orderengine.order.api.dto.PageOrderResponse;
import com.orderengine.order.api.mapper.OrderApiMapper;
import com.orderengine.order.domain.OrderEntity;
import com.orderengine.order.domain.OrderStatus;
import com.orderengine.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders")
@Tag(name = "Orders", description = "Create, get and list orders")
public class OrderController {

    private  static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create order", description = "Creates a new order for a customer with the specified items and shipping address.")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("createOrder customerId={} itemCount={}", request.customerId(), request.items().size());
        OrderEntity created = orderService.createOrder(
                request.customerId(),
                OrderApiMapper.toNewItems(request),
                OrderApiMapper.toShippingAddress(request.shippingAddress()),
                request.currency()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderApiMapper.toResponse(created));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by Id")
    public OrderResponse getOrder(@PathVariable String orderId) {
        return OrderApiMapper.toResponse(orderService.getOrder(orderId));
    }

    @GetMapping
    @Operation(summary = "List orders", description = "Paginated list; optional status filter")
    public PageOrderResponse listOrders(
            @RequestParam(required = false)OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return OrderApiMapper.toPageResponse(
                orderService.listOrders(status, PageRequest.of(page, size))
        );
    }
}
