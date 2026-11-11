package com.orderengine.orchestration.worker;

import com.orderengine.client.order.OrderServiceClient;
import com.orderengine.client.order.model.OrderResponse;
import com.orderengine.client.order.model.OrderStatus;
import com.orderengine.client.order.model.StatusUpdateRequest;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;

import java.util.Map;
import java.util.UUID;

public class UpdateOrderStatusToFraudReviewWorker extends AbstractJobWorker {

    private final OrderServiceClient orderServiceClient;

    UpdateOrderStatusToFraudReviewWorker(OrderServiceClient orderServiceClient) {
        this.orderServiceClient = orderServiceClient;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.UPDATE_ORDER_STATUS_TO_FRAUD_REVIEW;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        String orderId = String.valueOf(job.getVariablesAsMap().get(ActivityParameters.ORDER_ID));
        OrderResponse order = orderServiceClient.updateStatus(
                UUID.fromString(orderId),
                idempotencyKey(job),
                new StatusUpdateRequest().status(OrderStatus.FRAUD_REVIEW)
        );
        return Map.of(ActivityParameters.ORDER, order);
    }
}