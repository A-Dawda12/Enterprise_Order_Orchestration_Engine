package com.orderengine.order.service.impl;

import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import com.orderengine.order.domain.*;
import com.orderengine.order.repository.OrderEventRepository;
import com.orderengine.order.repository.OrderRepository;
import com.orderengine.order.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private static final String DEFAULT_CURRENCY = "INR";

    private final OrderRepository orderRepository;
    private final OrderEventRepository orderEventRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderEventRepository orderEventRepository) {
        this.orderRepository = orderRepository;
        this.orderEventRepository = orderEventRepository;
    }

    @Override
    @Transactional
    public OrderEntity createOrder(
            String customerId,
            List<NewOrderItem> items,
            ShippingAddress shippingAddress,
            String currency
    ) {
        BigDecimal total = items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setCustomerId(customerId);
        order.setStatus(OrderStatus.CREATED);
        order.setCurrency(currency == null || currency.isBlank() ? DEFAULT_CURRENCY : currency);
        order.setShippingAddress(shippingAddress);

        for(NewOrderItem item : items) {
            OrderItemEntity line = new OrderItemEntity();
            line.setSku(item.sku());
            line.setQuantity(item.quantity());
            line.setUnitPrice(item.unitPrice());
            order.addItem(line);
        }

        OrderEntity saved = orderRepository.save(order);
        appendEvent(saved.getOrderId(), "order.created", Map.of(
                "status", saved.getStatus().name(),
                "customerId", saved.getCustomerId(),
                "totalAmount", saved.getTotalAmount()
        ));
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderEntity getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderEngineException(ErrorCode.NOT_FOUND, "Order not found: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderEntity> listOrders(OrderStatus status, Pageable pageable) {
        if(status == null){
            return orderRepository.findAll(pageable);
        }
        return orderRepository.findByStatus(status, pageable);
    }

    private void appendEvent(String orderId, String eventType, Map<String, Object> payload) {
        OrderEventEntity event = new OrderEventEntity();
        event.setOrderId(orderId);
        event.setEventType(eventType);
        event.setPayload(payload);
        orderEventRepository.save(event);
    }
}
