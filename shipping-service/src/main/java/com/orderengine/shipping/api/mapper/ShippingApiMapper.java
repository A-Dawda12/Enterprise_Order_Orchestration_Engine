package com.orderengine.shipping.mapper;

import com.orderengine.shipping.api.dto.ShippingResponse;
import com.orderengine.shipping.domain.ShipmentEntity;

public final class ShippingApiMapper {

    private ShippingApiMapper() {

    }

    public static ShippingResponse toResponse(ShipmentEntity entity) {
        return new ShippingResponse(
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
