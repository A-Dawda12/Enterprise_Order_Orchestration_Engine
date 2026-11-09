package com.orderengine.orchestration.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.client.order.model.OrderResponse;
import com.orderengine.client.payment.model.AuthorizeRequest;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BuildAuthorizePaymentRequestWorker extends AbstractJobWorker{

    private final ObjectMapper objectMapper;

    BuildAuthorizePaymentRequestWorker(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String jobType(){
        return HandlerActivityName.BUILD_AUTHORIZE_PAYMENT_REQUEST;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job){
        Map<String, Object> variables = job.getVariablesAsMap();
        OrderResponse order = objectMapper.convertValue(variables.get(ActivityParameters.ORDER), OrderResponse.class);
        Object token = variables.get(ActivityParameters.PAYMENT_METHOD_TOKEN);

        AuthorizeRequest request = new AuthorizeRequest()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .amount(order.getTotalAmount())
                .currency(order.getCurrency())
                .paymentMethodToken(String.valueOf(token));
        return Map.of(ActivityParameters.AUTHORIZE_REQUEST, request);
    }
}
