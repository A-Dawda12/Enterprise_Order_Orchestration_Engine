package com.orderengine.shipping.api.mapper;

import com.orderengine.shipping.api.dto.ShipmentResponse;
import com.orderengine.shipping.domain.ShipmentEntity;

public final class ShippingApiMapper {

    private ShippingApiMapper() {

    }

    public static ShipmentResponse toResponse(ShipmentEntity entity) {
        return new ShipmentResponse(
                entity.getShipmentId(),
                entity.getOrderId(),
                entity.getStatus().name(),
                entity.getCarrier(),
                entity.getTrackingNumber(),
                entity.getLabelUrl(),
                entity.getCreatedAt()
        );
    }
}
