package com.orderengine.orchestration.worker;

import com.orderengine.client.order.OrderServiceClient;
import com.orderengine.client.order.model.ValidationResultResponse;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ValidateShippingAddressWorker extends AbstractJobWorker{

    private final OrderServiceClient orderServiceClient;

    ValidateShippingAddressWorker(OrderServiceClient orderServiceClient) {
        this.orderServiceClient = orderServiceClient;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.VALIDATE_SHIPPING_ORDER;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        String orderId = String.valueOf(variables.get(ActivityParameters.ORDER_ID));
        com.orderengine.client.order.model.CheckResultResponse validationResult = orderServiceClient.validateShippingAddress(
                UUID.fromString(orderId),
                idempotencyKey(job)
        );

        List<String> errors = validationResult.getErrors() == null ? List.of() : validationResult.getErrors();
        if(variables.get(ActivityParameters.VALIDATION_ERRORS) instanceof List<?> itemErrors) {
            itemErrors.forEach(e -> errors.add(String.valueOf(e)));
        }
        if(validationResult.getErrors() != null) {
            errors.addAll(validationResult.getErrors());
        }

        return Map.of(
                ActivityParameters.VALID_ADDRESS, Boolean.TRUE.equals(validationResult.getValid()),
                ActivityParameters.VALIDATION_ERRORS, errors
        );
    }
}
