package com.orderengine.orchestration.worker;

import com.orderengine.client.shipping.ShippingServiceClient;
import com.orderengine.client.shipping.model.CreateShipmentRequest;
import com.orderengine.client.shipping.model.ShipmentResponse;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class CreateShipmentInShippingServiceWorker extends AbstractJobWorker{

    private final ObjectMapper objectMapper;
    private final ShippingServiceClient shippingServiceClient;

    CreateShipmentInShippingServiceWorker(ObjectMapper objectMapper, ShippingServiceClient shippingServiceClient){
        this.objectMapper = objectMapper;
        this.shippingServiceClient = shippingServiceClient;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.CREATE_SHIPMENT_IN_SHIPPING_SERVICE;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        CreateShipmentRequest request = objectMapper.convertValue(
                job.getVariablesAsMap().get(ActivityParameters.CREATE_SHIPMENT_REQUEST),
                CreateShipmentRequest.class
        );

        ShipmentResponse shipment = shippingServiceClient.createShipment(
                idempotencyKey(job).toString(), request
        );

        return Map.of(ActivityParameters.SHIPMENT, shipment);
    }
}
