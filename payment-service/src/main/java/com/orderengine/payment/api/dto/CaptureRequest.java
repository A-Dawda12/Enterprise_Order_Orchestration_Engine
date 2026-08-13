package com.orderengine.payment.api.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record CaptureRequest(
        @DecimalMin(value = "0.01", inclusive = true) BigDecimal amount
) {
}
