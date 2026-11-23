package com.orderengine.orchestration.worker;

import com.orderengine.client.payment.PaymentServiceClient;
import com.orderengine.client.payment.model.PaymentResponse;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VoidPaymentInPaymentServiceWorker extends AbstractJobWorker {

    private final PaymentServiceClient paymentServiceClient;

    VoidPaymentInPaymentServiceWorker(PaymentServiceClient paymentServiceClient) {
        this.paymentServiceClient = paymentServiceClient;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.VOID_PAYMENT_IN_PAYMENT_SERVICE;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        Object paymentId = job.getVariablesAsMap().get(ActivityParameters.PAYMENT_ID);
        if(paymentId == null){
            throw new IllegalStateException("paymentId missing from process variables");
        }
        PaymentResponse payment = paymentServiceClient.voidAuthorization(
                String.valueOf(paymentId), idempotencyKey(job).toString()
        );

        return Map.of(ActivityParameters.PAYMENT, payment);
    }
}
