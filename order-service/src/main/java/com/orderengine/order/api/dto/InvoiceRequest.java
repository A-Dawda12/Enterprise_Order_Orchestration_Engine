package com.orderengine.order.api.dto;

import jakarta.validation.constraints.NotBlank;

public record InvoiceRequest(
        String orderId,
        @NotBlank String paymentId
) {
}
