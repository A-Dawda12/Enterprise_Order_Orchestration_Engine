package com.orderengine.orchestration.worker;

import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.api.response.ActivatedJob;

import java.util.Map;

public class ExtractFraudScoreWorker extends  AbstractJobWorker {

    private static final String SCORE = "score";
    private static final String REQUIRES_REVIEW = "requiresReview";

    @Override
    public String jobType() {
        return HandlerActivityName.EXTRACT_FRAUD_SCORE;
    }

    @Override
    protected Map<String, Object> execute(ActivatedJob job) {
        if(!(job.getVariablesAsMap().get(ActivityParameters.FRAUD_ASSESSMENT) instanceof Map<?, ?> assessment)
        || !(assessment.get(SCORE) instanceof Number score) || !(assessment.get(REQUIRES_REVIEW) instanceof Boolean requiresReview)) {
            throw new IllegalArgumentException("fraudAssessment.score/fraudAssessment.requiresReview missing from process variables");
        }

        return Map.of(ActivityParameters.FRAUD_SCORE, score.intValue(), ActivityParameters.REQUIRES_FRAUD_REVIEW, requiresReview);
    }
}
