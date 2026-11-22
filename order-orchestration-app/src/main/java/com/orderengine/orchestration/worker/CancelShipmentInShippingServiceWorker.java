package com.orderengine.orchestration.worker;

import com.orderengine.client.shipping.ShippingServiceClient;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CancelShipmentInShippingServiceWorker extends AbstractJobWorker {

    private final ShippingServiceClient shippingServiceClient;

    public CancelShipmentInShippingServiceWorker(ShippingServiceClient shippingServiceClient) {
        this.shippingServiceClient = shippingServiceClient;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.CANCEL_SHIPMENT_IN_SHIPPING_SERVICE;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        Object shipmentId = job.getVariablesAsMap().get(ActivityParameters.SHIPMENT_ID);
        if (shipmentId == null) {
            log.info("No shipmentId - nothing to cancel");
            return Map.of();
        }
        shippingServiceClient.cancelShipment(String.valueOf(shipmentId));
        return Map.of();
    }
}
