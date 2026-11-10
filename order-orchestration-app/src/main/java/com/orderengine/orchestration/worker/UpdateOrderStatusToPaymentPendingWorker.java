package com.orderengine.orchestration.worker;

import com.orderengine.client.order.OrderServiceClient;
import com.orderengine.client.order.model.OrderResponse;
import com.orderengine.client.order.model.OrderStatus;
import com.orderengine.client.order.model.StatusUpdateRequest;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class UpdateOrderStatusToPaymentPendingWorker extends AbstractJobWorker {

    private final OrderServiceClient orderServiceClient;

    UpdateOrderStatusToPaymentPendingWorker(OrderServiceClient orderServiceClient) {
        this.orderServiceClient = orderServiceClient;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.UPDATE_ORDER_STATUS_TO_PAYMENT_PENDING;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        String orderId = String.valueOf(job.getVariablesAsMap().get(ActivityParameters.ORDER_ID));
        OrderResponse order = orderServiceClient.updateStatus(
                UUID.fromString(orderId),
                idempotencyKey(job),
                new StatusUpdateRequest().status(OrderStatus.PAYMENT_PENDING)
        );
        return Map.of(ActivityParameters.ORDER, order);
    }
}

