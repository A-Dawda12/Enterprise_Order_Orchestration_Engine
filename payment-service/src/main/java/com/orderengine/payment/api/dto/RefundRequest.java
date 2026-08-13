package com.orderengine.payment.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record RefundRequest(
        @NotBlank String reason,
        @DecimalMin(value = "0.01", inclusive = true) BigDecimal amount
) {
}
