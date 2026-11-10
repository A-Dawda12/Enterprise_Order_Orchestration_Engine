package com.orderengine.orchestration.worker;

import com.orderengine.client.notification.model.SendNotificationRequest;
import com.orderengine.client.order.model.OrderResponse;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Component
public class BuildOrderShippedNotificationRequestWorker extends AbstractJobWorker{


    private final ObjectMapper objectMapper;

    public BuildOrderShippedNotificationRequestWorker(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.BUILD_ORDER_SHIPPED_NOTIFICATION_REQUEST;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        Map<String,Object> variables = job.getVariablesAsMap();
        OrderResponse order = objectMapper.convertValue(
                variables.get(ActivityParameters.ORDER), OrderResponse.class
        );

        Map<String, String> notificationVariables = new HashMap<>();
        notificationVariables.put("orderId", order.getOrderId());
        Object shipmentId = variables.get(ActivityParameters.SHIPMENT_ID);
        if (shipmentId != null) {
            notificationVariables.put("shipmentId", String.valueOf(shipmentId));
        }
        if (variables.get(ActivityParameters.SHIPMENT) instanceof Map<?,?> shipment
                && shipment.get("trackingNumber") != null
        ) {
            notificationVariables.put("trackingNumber", String.valueOf(shipment.get("trackingNumber")));
        }

        SendNotificationRequest request = new SendNotificationRequest()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .channel("EMAIL")
                .template("ORDER_SHIPPED")
                .variables(notificationVariables)
        ;
        return Map.of(ActivityParameters.NOTIFICATION_REQUEST, request);
    }
}
