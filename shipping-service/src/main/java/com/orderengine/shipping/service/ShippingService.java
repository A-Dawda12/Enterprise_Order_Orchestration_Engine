package com.orderengine.shipping.service;

import com.orderengine.shipping.domain.ShipmentEntity;

import java.util.List;

public interface ShippingService {

    ShipmentEntity createShipment(
            String orderId,
            String reservationId,
            ShippingAddress address,
            List<ShipmentItem> items
    );

    ShipmentEntity getShipment(String shipmentId);

    void cancelShipment(String shipmentId);

    record ShippingAddress(
            String line1,
            String city,
            String state,
            String postalCode,
            String country
    ){
    }

    record ShipmentItem(String sku, int quantity) {}

}
