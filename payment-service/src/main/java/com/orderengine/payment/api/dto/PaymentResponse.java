package com.orderengine.payment.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String paymentId,
        String orderId,
        String status,
        BigDecimal authorizedAmount,
        BigDecimal capturedAmount,
        String currency,
        Instant authorizedAt
) {
}
