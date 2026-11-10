package com.orderengine.orchestration.worker;

import com.orderengine.client.order.OrderServiceClient;
import com.orderengine.client.order.model.InvoiceRequest;
import com.orderengine.client.order.model.InvoiceResponse;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;


import java.util.Map;
import java.util.UUID;

@Component
public class GenerateInvoiceInOrderServiceWorker extends AbstractJobWorker{


    private final OrderServiceClient orderServiceClient;

    public GenerateInvoiceInOrderServiceWorker(OrderServiceClient orderServiceClient) {
        this.orderServiceClient = orderServiceClient;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.GENERATE_INVOICE_IN_ORDER_SERVICE;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        Object paymentId = variables.get(ActivityParameters.PAYMENT_ID);
        if(paymentId == null){
            throw new IllegalStateException("Payment Id is not present in the variables");
        }

        String orderId = String.valueOf(variables.get(ActivityParameters.ORDER_ID));

        InvoiceResponse invoice = orderServiceClient.generateInvoice(
                UUID.fromString(orderId),
                idempotencyKey(job),
                new InvoiceRequest()
                        .orderId(orderId)
                        .paymentId(String.valueOf(paymentId))
        );
        return Map.of(ActivityParameters.INVOICE, invoice);
    }
}
