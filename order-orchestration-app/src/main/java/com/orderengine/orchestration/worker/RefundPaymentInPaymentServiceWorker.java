package com.orderengine.orchestration.worker;

import com.orderengine.client.payment.PaymentServiceClient;
import com.orderengine.client.payment.model.RefundRequest;
import com.orderengine.client.payment.model.RefundResponse;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RefundPaymentInPaymentServiceWorker extends AbstractJobWorker {

    private final PaymentServiceClient paymentServiceClient;

    RefundPaymentInPaymentServiceWorker(PaymentServiceClient paymentServiceClient) {
        this.paymentServiceClient = paymentServiceClient;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.REFUND_PAYMENT_IN_PAYMENT_SERVICE;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        Object paymentId = variables.get(ActivityParameters.PAYMENT_ID);
        if(paymentId == null){
            throw new IllegalStateException("paymentId missing from process variables");
        }
        Object reason = variables.get(ActivityParameters.COMPENSATION_REASON);
        RefundResponse refund = paymentServiceClient.refund(
                String.valueOf(paymentId),
                idempotencyKey(job).toString(),
                new RefundRequest().reason(reason == null ? "ORDER_CANCELLED" : String.valueOf(reason))
        );
        return Map.of(ActivityParameters.REFUND, refund);
    }
}
