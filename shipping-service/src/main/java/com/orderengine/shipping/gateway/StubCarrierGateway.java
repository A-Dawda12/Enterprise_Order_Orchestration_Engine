package com.orderengine.shipping.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class StubCarrierGateway implements CarrierGateway{

    private static final String CARRIER = "UPS";

    @Override
    public LabelResult createLabel(String shipmentId, String orderId, String postalCode, String country) {
        String trackingNumber = "1Z" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        String labelUrl = "https://carrier.example.com/labels/" + trackingNumber + ".pdf";

        log.info("stub createLabell shipmentId={} orderId={} postalCode={} country={} -> tracking={}",
                shipmentId, orderId, postalCode, country, trackingNumber);
        return new LabelResult(CARRIER, trackingNumber, labelUrl);
    }

    @Override
    public void cancelLabel(String trackingNumber) {
        log.info("stub cancelLabel trackingNumber={}", trackingNumber);
    }
}
