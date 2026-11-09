package com.orderengine.orchestration.worker;

import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class ExtractPaymentIdWorker extends AbstractJobWorker{

    @Override
    public String jobType() {
        return HandlerActivityName.EXTRACT_PAYMENT_ID;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        if(!(job.getVariablesAsMap().get(ActivityParameters.PAYMENT) instanceof Map<?,?> reservation)
                || reservation.get(ActivityParameters.PAYMENT_ID) == null
        ) {
            throw new IllegalArgumentException("payment.paymentId missing from process variables");
        }
        return Map.of(ActivityParameters.PAYMENT_ID, String.valueOf(reservation.get(ActivityParameters.PAYMENT_ID)));
    }
}

