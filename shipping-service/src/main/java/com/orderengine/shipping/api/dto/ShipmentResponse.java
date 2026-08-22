package com.orderengine.shipping.api.dto;

import java.time.Instant;

public record ShipmentResponse(
        String shipmentId,
        String orderId,
        String status,
        String carrier,
        String trackingNumber,
        String labelUrl,
        Instant createdAt
){
}
