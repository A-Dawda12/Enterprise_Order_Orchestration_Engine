package com.orderengine.order.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record InvoiceResponse(
        String invoiceId,
        String orderId,
        String pdfUrl,
        BigDecimal amount,
        Instant issuedAt
) {
}
