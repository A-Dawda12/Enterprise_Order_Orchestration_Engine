package com.orderengine.orchestration.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.client.fraud.model.FraudAssessmentRequest;
import com.orderengine.client.fraud.model.FraudShippingAddressRequest;
import com.orderengine.client.order.model.OrderResponse;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BuildFraudAssessmentRequestWorker extends AbstractJobWorker {

    private final ObjectMapper objectMapper;

    BuildFraudAssessmentRequestWorker(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String jobType(){
        return HandlerActivityName.BUILD_FRAUD_ASSESSMENT_REQUEST;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job){
        Map<String, Object> variables = job.getVariablesAsMap();
        OrderResponse order = objectMapper.convertValue(
                variables.get(ActivityParameters.ORDER), OrderResponse.class
        );
        Object token = variables.get(ActivityParameters.PAYMENT_METHOD_TOKEN);
        if(token == null){
            throw new IllegalStateException("paymentMethodToken missing from process vatiables");
        }

        FraudAssessmentRequest request = new FraudAssessmentRequest()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .amount(order.getTotalAmount())
                .shippingAddress( new FraudShippingAddressRequest()
                        .country(order.getShippingAddress().getCountry())
                        .postalCode(order.getShippingAddress().getPostalCode()))
                .paymentMethodToken(String.valueOf(token));

        return Map.of(ActivityParameters.FRAUD_REQUEST, request);
    }
}
