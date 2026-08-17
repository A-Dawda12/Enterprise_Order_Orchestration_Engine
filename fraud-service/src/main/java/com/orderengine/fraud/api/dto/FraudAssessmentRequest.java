package com.orderengine.fraud.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FraudAssessmentRequest(
        @NotBlank String orderId,
        @NotBlank String customerId,
        @NotNull @DecimalMin(value = "0.01", inclusive = false)BigDecimal amount,
        @Valid @NotNull ShippingAddressRequest shippingAddress,
        @NotBlank String paymentMethodToken
        ) {

    public record ShippingAddressRequest(
            @NotBlank String country,
            @NotBlank String postalCode
    ){
    }
}
