package com.orderengine.orchestration.worker;

import com.orderengine.client.order.OrderServiceClient;
import com.orderengine.client.order.model.OrderResponse;
import com.orderengine.client.order.model.OrderStatus;
import com.orderengine.client.order.model.StatusUpdateRequest;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.model.bpmn.instance.Activity;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class UpdateOrderStatusToCompensatingWorker extends AbstractJobWorker{

    private final OrderServiceClient orderServiceClient;

    UpdateOrderStatusToCompensatingWorker(OrderServiceClient orderServiceClient) {
        this.orderServiceClient = orderServiceClient;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.UPDATE_ORDER_STATUS_TO_COMPENSATING;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        String orderId = String.valueOf(variables.get(ActivityParameters.ORDER_ID));
        Object reason = variables.get(ActivityParameters.COMPENSATION_REASON);
        OrderResponse order = orderServiceClient.updateStatus(
                UUID.fromString(orderId),
                idempotencyKey(job),
                new StatusUpdateRequest()
                        .status(OrderStatus.COMPENSATING)
                        .reason(reason == null ? null : String.valueOf(reason))
        );
        return Map.of(ActivityParameters.ORDER, order);
    }
}
