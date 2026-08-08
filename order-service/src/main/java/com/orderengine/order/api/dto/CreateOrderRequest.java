package com.orderengine.order.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        @NotBlank String customerId,
        @NotEmpty @Valid List<OrderItemRequest> items,
        @NotNull @Valid ShippingAddressRequest shippingAddress,
        String currency
) {
    public record OrderItemRequest(
            @NotBlank String sku,
            @Min(1) int quantity,
            @NotNull @DecimalMin("0.01")BigDecimal unitPrice
    ){
    }

    public record ShippingAddressRequest(
            @NotBlank String line1,
            String line2,
            @NotBlank String city,
            @NotBlank String state,
            @NotBlank String postalCode,
            @NotBlank String country
    ){
    }
}
