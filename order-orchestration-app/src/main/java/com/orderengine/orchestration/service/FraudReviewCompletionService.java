package com.orderengine.orchestration.service;

import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class FraudReviewCompletionService {

    private final ZeebeClient zeebeClient;
    private static final String USER_TASK_JOB_TYPE = "io.camunnda.zeebe:userTask";

    public FraudReviewCompletionService(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    public void complete(UUID orderId, String decision, String reviewerId, String notes) {
        String normalized = decision == null ? "" : decision.trim().toUpperCase(Locale.ROOT);

        if(!"APPROVED".equals(normalized) && !"REJECTED".equals(normalized)) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "decision must be either APPROVED or REJECTED");
        }

        var activated = zeebeClient.newActivateJobsCommand()
                .jobType(USER_TASK_JOB_TYPE)
                .maxJobsToActivate(32)
                .timeout(
                        Duration.ofSeconds(5)
                )
                .workerName("fraud-review-local-completer")
                .fetchVariables(
                        ActivityParameters.ORDER_ID,
                        ActivityParameters.FRAUD_SCORE,
                        ActivityParameters.ASSESSMENT_ID
                )
                .send().join();

        ActivatedJob match = null;
        for(ActivatedJob job : activated.getJobs()) {
            if(HandlerActivityName.FRAUD_MANAGER_REVIEW.equals(job.getElementId())
            && orderId.toString().equals(String.valueOf(job.getVariablesAsMap().get(ActivityParameters.ORDER_ID)))) {
                match = job;
                break;
            }
        }
        if(match == null){
            throw new OrderEngineException(ErrorCode.NOT_FOUND,
                "No FraudManagerReview user task found for orderId: " + orderId
                + " (is requiresFraudReview true and the instance waiting on the task?)");
        }

        Map<String, Object> varaibles = new HashMap<>();
        varaibles.put(ActivityParameters.FRAUD_DECISION, normalized);
        varaibles.put(
                ActivityParameters.REVIEWER_ID,
                reviewerId == null || reviewerId.isBlank() ? "fraud-manaager" : reviewerId
        );
        if(notes != null && !notes.isBlank()) {
            varaibles.put(ActivityParameters.REVIEW_NOTES, notes);
        }

        zeebeClient.newCompleteCommand(match.getKey())
                .variables(varaibles)
                .send().join();
        log.info("Completed FraudManagerReview user task for orderId: {} with decision: {}", orderId, normalized);
    }
}
