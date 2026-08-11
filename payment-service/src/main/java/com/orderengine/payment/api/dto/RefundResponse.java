package com.orderengine.payment.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record RefundResponse(
        String refundId,
        String paymentId,
        String status,
        BigDecimal refundedAmount,
        Instant refundedAt
) {
}
