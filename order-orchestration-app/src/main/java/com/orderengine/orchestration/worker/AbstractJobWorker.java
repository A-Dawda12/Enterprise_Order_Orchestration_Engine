package com.orderengine.orchestration.worker;

import com.orderengine.common.orchestration.ActivityParameters;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Slf4j
public abstract class AbstractJobWorker implements JobHandler {

    private static final String RETRY_BACKOFF_HEADER = "retryBackoff";

    public abstract String jobType();

    protected abstract Map<String, Object> execute(ActivatedJob job);

    protected static UUID idempotencyKey(ActivatedJob job) {
        return UUID.nameUUIDFromBytes(String.valueOf(job.getKey()).getBytes(StandardCharsets.UTF_8));
    }

    private static Duration retryBackoff(ActivatedJob job) {
        String value = job.getCustomHeaders().get(RETRY_BACKOFF_HEADER);
        return value == null || value.isBlank() ? Duration.ZERO : Duration.parse(value.trim());
    }

    @Override
    public final void handle(JobClient client, ActivatedJob job) {
        String orderId = String.valueOf(job.getVariablesAsMap().get(ActivityParameters.ORDER_ID));
        MDC.put("orderId", orderId);
        MDC.put("activity", job.getType());

        long startAt = System.currentTimeMillis();
        log.info("JOB IN activity={} orderId={}", job.getType(), orderId);
        try{
            Map<String, Object> outputVariables = execute(job);
            Map<String, Object> output = outputVariables == null ? Map.of() : outputVariables;
            client.newCompleteCommand(job.getKey())
                    .variables(output)
                    .send()
                    .join();
            log.info("JOB OUT activity={} orderId={} durationMs={} output={}", job.getType(), orderId, System.currentTimeMillis() - startAt, output);
        }catch (Exception ex) {

            int retriesLeft = Math.max(job.getRetries() - 1, 0);
            Duration backoff = retryBackoff(job);
            log.error("JOB OUT activity={} orderId={} status=FAILED durationMs={} retriesLeft={} backoff={} reason={}", job.getType(), orderId, System.currentTimeMillis() - startAt, retriesLeft, backoff, ex.getMessage(), ex);

            client.newFailCommand(job.getKey())
                    .retries(retriesLeft)
                    .retryBackoff(backoff)
                    .errorMessage(String.valueOf(ex.getMessage()))
                    .send()
                    .join();
        }finally {
            MDC.remove("orderId");
            MDC.remove("activity");
        }
    }
}
