package com.orderengine.inventory.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ReserveRequest(
        @NotBlank String orderId,
        @NotEmpty @Valid List<ItemRequest> items
        ) {
    public record ItemRequest(
            @NotBlank String sku,
            @Min(1) int quantity
    ){
    }
}
