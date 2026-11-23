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
public class BuildOrderCancelledNotificationRequestWorker extends AbstractJobWorker {

    private final ObjectMapper objectMapper;

    public BuildOrderCancelledNotificationRequestWorker(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.BUILD_ORDER_CANCELLED_NOTIFICATION_REQUEST;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        OrderResponse order = objectMapper.convertValue(variables.get(ActivityParameters.ORDER), OrderResponse.class);

        Map<String, String> notificationVariables = new HashMap<>();
        notificationVariables.put(ActivityParameters.ORDER_ID, order.getOrderId());
        Object reason = variables.get(ActivityParameters.COMPENSATION_REASON);
        if(reason != null) {
            notificationVariables.put("reason", reason.toString());
        }

        SendNotificationRequest request = new SendNotificationRequest()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .channel("EMAIL")
                .template("ORDER_CANCELLED")
                .variables(notificationVariables);
        return Map.of(ActivityParameters.NOTIFICATION_REQUEST, request);
    }
}
