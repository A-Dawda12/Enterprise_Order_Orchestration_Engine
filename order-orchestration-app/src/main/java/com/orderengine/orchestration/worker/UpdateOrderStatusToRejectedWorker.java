package com.orderengine.orchestration.worker;

import com.orderengine.client.order.OrderServiceClient;
import com.orderengine.client.order.model.OrderResponse;
import com.orderengine.client.order.model.OrderStatus;
import com.orderengine.client.order.model.StatusUpdateRequest;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class UpdateOrderStatusToRejectedWorker extends AbstractJobWorker{

    private final OrderServiceClient orderServiceClient;

    UpdateOrderStatusToRejectedWorker(OrderServiceClient orderServiceClient){
        this.orderServiceClient = orderServiceClient;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.UPDATE_ORDER_STATUS_TO_REJECTED;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        String orderId = String.valueOf(variables.get(ActivityParameters.ORDER_ID));
        String reason = variables.get(ActivityParameters.VALIDATION_ERRORS) instanceof List<?> errors
                ? errors.stream().map(String::valueOf).collect(Collectors.joining(", "))
                : null;

        OrderResponse order = orderServiceClient.updateStatus(
                UUID.fromString(orderId),
                idempotencyKey(job),
                new StatusUpdateRequest().status(
                        OrderStatus.REJECTED).reason(reason)
        );
        return Map.of(ActivityParameters.ORDER, order);
    }
}
