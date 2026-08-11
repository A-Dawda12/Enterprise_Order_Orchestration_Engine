package com.orderengine.inventory.api.dto;

public record StockResponse(
        String sku,
        int availableQuantity,
        int reservedQuantity
) {
}
