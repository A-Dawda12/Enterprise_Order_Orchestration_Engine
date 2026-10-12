package com.orderengine.client.inventory;

import com.orderengine.client.inventory.api.InventoryApi;
import com.orderengine.client.inventory.model.ReservationResponse;
import com.orderengine.client.inventory.model.ReserveRequest;
import com.orderengine.client.inventory.model.StockResponse;
import org.springframework.stereotype.Component;

@Component
public class InventoryServiceClient {

    private final InventoryApi inventoryApi;

    public InventoryServiceClient(InventoryApi inventoryApi) {
        this.inventoryApi = inventoryApi;
    }

    public StockResponse getStock(String sku) {
        return inventoryApi.getStock(sku);
    }

    public ReservationResponse reserve(
            String idempotencyKey,
            ReserveRequest request
    ) {
        return inventoryApi.reserve(idempotencyKey, request);
    }

    public void release(String reservationId) {
        inventoryApi.release(reservationId);
    }

    public ReservationResponse confirm(
            String reservationId,
            String idempotencyKey
    ) {
        return inventoryApi.confirm(reservationId, idempotencyKey);
    }
}