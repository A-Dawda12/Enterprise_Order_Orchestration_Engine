package com.orderengine.order.service;

import com.orderengine.order.domain.OrderEntity;
import com.orderengine.order.domain.OrderStatus;
import com.orderengine.order.domain.ShippingAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    OrderEntity createOrder(
            String customerId,
            List<NewOrderItem> items,
            ShippingAddress shippingAddress,
            String currency
    );

    OrderEntity getOrder(String orderId);

    Page<OrderEntity> listOrders(OrderStatus status, Pageable pageable);

    record NewOrderItem(String sku, int quantity, BigDecimal unitPrice) {}
}
