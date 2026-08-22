package com.orderengine.shipping.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateShipmentRequest(
        @NotBlank String orderId,
        String reservationId,
        @Valid @NotNull ShippingAddressRequest shippingAddress,
        @NotEmpty @Valid List<ItemRequest> items
){

    public record ShippingAddressRequest(
            @NotBlank String line1,
            @NotBlank String city,
            @NotBlank String state,
            @NotBlank String postalCode,
            @NotBlank String country
    ) {
    }

    public record ItemRequest(
            @NotBlank String sku,
            @Min(1) int quantity
    ) {
    }
}
