package com.orderengine.orchestration.worker;

import com.orderengine.client.order.model.OrderResponse;
import com.orderengine.client.order.model.ShippingAddressRequest;
import com.orderengine.client.shipping.model.CreateShipmentRequest;
import com.orderengine.client.shipping.model.ShipmentItemRequest;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class BuildCreateShippingRequestWorker extends AbstractJobWorker{

    private final ObjectMapper objectMapper;

    BuildCreateShippingRequestWorker(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.BUILD_CREATE_SHIPMENT_REQUEST;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        OrderResponse order = objectMapper.convertValue(variables.get(ActivityParameters.ORDER), OrderResponse.class);

        ShippingAddressRequest address = order.getShippingAddress();
        if(address == null){
            throw new IllegalStateException("Shipping address is missing from process variables");
        }
        Object reservationId = variables.get(ActivityParameters.RESERVATION_ID);
        CreateShipmentRequest request = new CreateShipmentRequest()
                .orderId(order.getOrderId())
                .reservationId(reservationId == null ? null :  String.valueOf(reservationId))
                .shippingAddress(new com.orderengine.client.shipping.model.ShippingAddressRequest()
                        .line1(address.getLine1())
                        .city(address.getCity())
                        .state(address.getState())
                        .postalCode(address.getPostalCode())
                        .country(address.getCountry()))
                .items(order.getItems().stream()
                        .map(item -> new ShipmentItemRequest()
                                .sku(item.getSku())
                                .quantity(item.getQuantity())
                        ).toList());
        return Map.of(ActivityParameters.CREATE_SHIPMENT_REQUEST, request);
    }
}
