package com.orderengine.orchestration.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.client.fraud.FraudServiceClient;
import com.orderengine.client.fraud.model.FraudAssessmentRequest;
import com.orderengine.client.fraud.model.FraudAssessmentResponse;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;

import java.util.Map;

public class AssessFraudInFraudServiceWorker extends AbstractJobWorker {

    private final FraudServiceClient fraudServiceClient;
    private final ObjectMapper objectMapper;

    public AssessFraudInFraudServiceWorker(FraudServiceClient fraudServiceClient, ObjectMapper objectMapper) {
        this.fraudServiceClient = fraudServiceClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.EXTRACT_RESERVATION_ID;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        FraudAssessmentRequest request = objectMapper.convertValue(
                job.getVariablesAsMap().get(ActivityParameters.FRAUD_REQUEST),
                FraudAssessmentRequest.class
        );

        FraudAssessmentResponse assessment = fraudServiceClient.assess(
                idempotencyKey(job).toString(), request
        );

        return Map.of(ActivityParameters.FRAUD_ASSESSMENT, assessment);
    }
}
