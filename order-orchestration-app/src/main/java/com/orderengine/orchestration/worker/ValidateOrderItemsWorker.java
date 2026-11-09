package com.orderengine.orchestration.worker;

import com.orderengine.client.order.OrderServiceClient;
import com.orderengine.client.order.model.CheckResultResponse;
import com.orderengine.client.order.model.ValidationResultResponse;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ValidateOrderItemsWorker extends AbstractJobWorker{

    private final OrderServiceClient orderServiceClient;

    ValidateOrderItemsWorker(OrderServiceClient orderServiceClient) {
        this.orderServiceClient = orderServiceClient;
    }

    @Override
    public String jobType(){
        return HandlerActivityName.VALIDATE_ORDER_ITMES;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        String orderId = String.valueOf(job.getVariablesAsMap().get(ActivityParameters.ORDER_ID));
        CheckResultResponse result = orderServiceClient.validateOrderItems(
                UUID.fromString(orderId),
                idempotencyKey(job)
        );
        List<String> errors = result.getErrors() == null ? List.of() : result.getErrors();
        return Map.of(
                ActivityParameters.VALID_ITEMS, Boolean.TRUE.equals(result.getValid()),
                ActivityParameters.VALIDATION_ERRORS, errors
        );
    }
}
