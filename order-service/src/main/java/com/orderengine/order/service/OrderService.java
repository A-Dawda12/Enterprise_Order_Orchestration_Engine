package com.orderengine.order.service;

import com.orderengine.order.domain.InvoiceEntity;
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

    ValidationResult validateOrder(String orderId);

    InvoiceEntity generateInvoice(String orderId, String paymentId);

    OrderEntity updateStatus(String orderId, OrderStatus status, String reason);

    record ValidationResult(
        boolean valid,
        boolean validItems,
        boolean validAddress,
        List<String> errors
    ){
    }

    record NewOrderItem(String sku, int quantity, BigDecimal unitPrice) {}
}
