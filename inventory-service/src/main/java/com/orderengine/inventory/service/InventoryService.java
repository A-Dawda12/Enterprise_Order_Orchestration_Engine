package com.orderengine.inventory.service;

import com.orderengine.inventory.domain.InventoryEntity;
import com.orderengine.inventory.domain.ReservationEntity;

import java.util.List;

public interface InventoryService {

    InventoryEntity getStock(String sku);

    ReservationEntity reserve(String orderId, List<ReserveItem> items);

    void release(String reservationId);

    ReservationEntity confirm(String reservationId);

    record ReserveItem(String sku, int quantity){}
}
