package com.orderengine.payment.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AuthorizeRequest(
    @NotBlank String orderId,
    @NotBlank String customerId,
    @NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal amount,
    @NotBlank @Size(min = 3, max = 3) String currency,
    @NotBlank String paymentMethodToken
) {
}
