
package com.orderengine.orchestration.worker;

import com.orderengine.client.fraud.FraudServiceClient;
import com.orderengine.client.fraud.model.ReviewDecisionRequest;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * IMPLEMENTATION-PATTERNS §4.3, row 21.
 * Persists the Tasklist / local-complete decision to fraud-service.
 * Expects {@code fraudDecision} (+ optional {@code reviewerId}, {@code reviewNotes}) from the user task.
 */
@Component
class RecordFraudReviewDecisionWorker extends AbstractJobWorker {

    private final FraudServiceClient fraudServiceClient;

    RecordFraudReviewDecisionWorker(FraudServiceClient fraudServiceClient) {
        this.fraudServiceClient = fraudServiceClient;
    }

    @Override
    public String jobType() {
        return HandlerActivityName.RECORD_FRAUD_REVIEW_DECISION;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();

        Object assessmentId = variables.get(ActivityParameters.ASSESSMENT_ID);

        if (assessmentId == null
                && variables.get(ActivityParameters.FRAUD_ASSESSMENT) instanceof Map<?, ?> assessment) {
            assessmentId = assessment.get(ActivityParameters.ASSESSMENT_ID);
        }

        if (assessmentId == null) {
            throw new IllegalStateException("assessmentId missing from process variables");
        }

        Object rawDecision = variables.get(ActivityParameters.FRAUD_DECISION);

        if (rawDecision == null || String.valueOf(rawDecision).isBlank()) {
            throw new IllegalStateException(
                    "fraudDecision missing from process variables (complete the user task first)");
        }

        String decision = String.valueOf(rawDecision)
                .trim()
                .toUpperCase(Locale.ROOT);

        ReviewDecisionRequest.DecisionEnum decisionEnum = switch (decision) {
            case "APPROVED" -> ReviewDecisionRequest.DecisionEnum.APPROVED;
            case "REJECTED" -> ReviewDecisionRequest.DecisionEnum.REJECTED;
            default -> throw new IllegalStateException(
                    "fraudDecision must be APPROVED or REJECTED, got: " + decision);
        };

        Object reviewer = variables.get(ActivityParameters.REVIEWER_ID);

        String reviewerId = reviewer == null || String.valueOf(reviewer).isBlank()
                ? "fraud-manager"
                : String.valueOf(reviewer);

        Object notes = variables.get(ActivityParameters.REVIEW_NOTES);

        ReviewDecisionRequest request = new ReviewDecisionRequest()
                .decision(decisionEnum)
                .reviewerId(reviewerId);

        if (notes != null && !String.valueOf(notes).isBlank()) {
            request.notes(String.valueOf(notes));
        }

        fraudServiceClient.recordDecision(
                String.valueOf(assessmentId),
                idempotencyKey(job).toString(),
                request);

        Map<String, Object> output = new HashMap<>();
        output.put(ActivityParameters.FRAUD_DECISION, decision);
        output.put(ActivityParameters.REVIEWER_ID, reviewerId);

        if ("REJECTED".equals(decision)) {
            output.put(ActivityParameters.COMPENSATION_REASON, "FRAUD_REJECTED");
        }

        return output;
    }
}
