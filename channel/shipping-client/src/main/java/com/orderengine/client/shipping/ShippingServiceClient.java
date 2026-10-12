package com.orderengine.client.shipping;

import com.orderengine.client.shipping.api.ShippingApi;
import com.orderengine.client.shipping.model.CreateShipmentRequest;
import com.orderengine.client.shipping.model.ShipmentResponse;
import org.springframework.stereotype.Component;

@Component
public class ShippingServiceClient {

    private final ShippingApi shippingApi;

    public ShippingServiceClient(ShippingApi shippingApi) {
        this.shippingApi = shippingApi;
    }

    public ShipmentResponse getShipment(String shipmentId) {
        return shippingApi.getShipment(shipmentId);
    }

    public ShipmentResponse createShipment(String idempotencyKey, CreateShipmentRequest request) {
        return shippingApi.createShipment(idempotencyKey, request);
    }

    public void cancelShipment(String shipmentId) {
        shippingApi.cancelShipment(shipmentId);
    }
}