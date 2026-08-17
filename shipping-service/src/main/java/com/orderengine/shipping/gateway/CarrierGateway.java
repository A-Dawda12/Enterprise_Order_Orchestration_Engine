package com.orderengine.shipping.gateway;

public interface CarrierGateway {

    LabelResult createLabel(String shipmentId, String ordrId, String postalCode, String country);

    void cancelLabel(String trackingNumber);

    record LabelResult(String carrier, String trackingNumber, String labelUrl) {
    }
}
