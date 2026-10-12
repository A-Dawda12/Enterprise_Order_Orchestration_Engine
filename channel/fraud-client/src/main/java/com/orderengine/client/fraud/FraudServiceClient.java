package com.orderengine.client.fraud;

import com.orderengine.client.fraud.api.FraudApi;
import com.orderengine.client.fraud.model.FraudAssessmentRequest;
import com.orderengine.client.fraud.model.FraudAssessmentResponse;
import com.orderengine.client.fraud.model.ReviewDecisionRequest;
import org.springframework.stereotype.Component;

@Component
public class FraudServiceClient {

    private final FraudApi fraudApi;

    public FraudServiceClient(FraudApi fraudApi) {
        this.fraudApi = fraudApi;
    }

    public FraudAssessmentResponse assess(String idempotencyKey, FraudAssessmentRequest request) {
        return fraudApi.assess(idempotencyKey, request);
    }

    public FraudAssessmentResponse getAssessment(String assessmentId) {
        return fraudApi.getAssessment(assessmentId);
    }

    public FraudAssessmentResponse recordDecision(
            String assessmentId,
            String idempotencyKey,
            ReviewDecisionRequest request
    ) {
        return fraudApi.recordDecision(assessmentId, idempotencyKey, request);
    }
}