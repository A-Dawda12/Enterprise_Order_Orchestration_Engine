package com.orderengine.inventory.mapper;

import com.orderengine.inventory.api.dto.ReservationResponse;
import com.orderengine.inventory.api.dto.StockResponse;
import com.orderengine.inventory.domain.InventoryEntity;
import com.orderengine.inventory.domain.ReservationStatus;

import java.util.List;

public class InventoryApiMapper {

    private InventoryApiMapper() {
    }

    public static StockResponse toStockResponse(InventoryEntity entity) {
        return new StockResponse(
                entity.getSku(),
                entity.getAvailableQuantity(),
                entity.getReservedQuantity()
        );
    }

    public static ReservationResponse toReservationResponse(com.orderengine.inventory.domain.ReservationEntity entity) {
        boolean reserved = entity.getStatus() == ReservationStatus.RESERVED
                || entity.getStatus() == ReservationStatus.CONFIRMED;
        List<ReservationResponse.ItemResponse> items = entity.getItems().stream()
                .map(item -> new ReservationResponse.ItemResponse(
                        item.getSku(),
                        item.getQuantity(),
                        reserved
                ))
                .toList();
        return new ReservationResponse(
                entity.getReservationId(),
                entity.getOrderId(),
                entity.getStatus().name(),
                entity.getExpiresAt(),
                items
        );
    }

}
