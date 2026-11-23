package com.orderengine.shipping.gateway;

import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class StubCarrierGateway implements CarrierGateway{

    private static final String CARRIER = "UPS";

    private final int failFirstCalls;
    private final AtomicInteger createLabelCalls = new AtomicInteger(0);

    public StubCarrierGateway(@Value("${shipping.stub.fail-first-n-calls:0}") int failFirstCalls) {
        this.failFirstCalls = failFirstCalls;
    }

    @Override
    public LabelResult createLabel(String shipmentId, String orderId, String postalCode, String country) {
        int call = createLabelCalls.incrementAndGet();
        if(call <= failFirstCalls){
            log.warn("stud createLabel simulated carrier failure call={}/{} orderId={}", call, failFirstCalls, orderId);
            throw new OrderEngineException(ErrorCode.SERVICE_UNAVAILABLE, "Carrier unavaiable (stub failure " + call + ")");
        }
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
