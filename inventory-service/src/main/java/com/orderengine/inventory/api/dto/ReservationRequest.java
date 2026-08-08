package com.orderengine.inventory.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ReservationRequest(
        @NotBlank String orderId,
        @NotBlank @Valid List<ItemRequest> items
        ) {
    public record ItemRequest(
            @NotBlank String sku,
            @Min(1) int quantity
    ){
    }
}
