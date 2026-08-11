package com.orderengine.inventory.api.dto;

import java.time.Instant;
import java.util.List;

public record ReservationResponse(
        String reservationId,
        String orderId,
        String status,
        Instant expiresAt,
        List<ItemResponse> items
) {
    public record ItemResponse(String sku, int quantity, boolean reserved) {
    }
}
