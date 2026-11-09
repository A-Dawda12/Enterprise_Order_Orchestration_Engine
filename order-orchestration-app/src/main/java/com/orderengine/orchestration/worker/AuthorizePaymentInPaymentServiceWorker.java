package com.orderengine.orchestration.worker;

import com.orderengine.client.payment.PaymentServiceClient;
import com.orderengine.client.payment.model.AuthorizeRequest;
import com.orderengine.client.payment.model.PaymentResponse;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class AuthorizePaymentInPaymentServiceWorker extends AbstractJobWorker{

    private final PaymentServiceClient paymentServiceClient;
    private final ObjectMapper objectMapper;

    AuthorizePaymentInPaymentServiceWorker(PaymentServiceClient paymentServiceClient, ObjectMapper objectMapper) {
        this.paymentServiceClient = paymentServiceClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.AUTHORIZE_PAYMENT_IN_PAYMENT_SERVICE;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        AuthorizeRequest request = objectMapper.convertValue(
                job.getVariablesAsMap().get(ActivityParameters.AUTHORIZE_REQUEST),
                AuthorizeRequest.class
        );

        PaymentResponse payment = paymentServiceClient.authorize(
                idempotencyKey(job).toString(), request
        );
        return Map.of(ActivityParameters.PAYMENT, payment);
    }
}
