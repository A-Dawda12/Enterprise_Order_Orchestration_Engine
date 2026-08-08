package com.orderengine.order.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        String orderId,
        String customerId,
        String status,
        BigDecimal totalAmount,
        String currency,
        String workflowInstanceKey,
        List<OrderItemResponse> items,
        ShippingAddressResponse shippingAddress,
        Instant createdAt,
        Instant updatedAt
) {
    public record OrderItemResponse(
            String sku,
            int quality,
            BigDecimal unitPrice
    ){
    }

    public record ShippingAddressResponse(
            String line1,
            String line2,
            String city,
            String state,
            String postalCode,
            String country
    ){
    }
}
