package com.orderengine.orchestration.worker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JobWorkerRegistrar {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    private static final int DEFAULT_MAX_JOBS_ACTIVE = 32;

    private final ZeebeClient zeebeClient;
    private final List<AbstractJobWorker> workers;
    private final List<JobWorker> openWorker = new ArrayList<>();

    JobWorkerRegistrar(ZeebeClient zeebeClient, List<AbstractJobWorker> workers) {
        this.zeebeClient = zeebeClient;
        this.workers = workers;
    }

    @EventListener(ApplicationReadyEvent.class)
    void openWorkers() {
        for(AbstractJobWorker worker : workers) {
            JobWorker opened = zeebeClient.newWorker()
                    .jobType(worker.jobType())
                    .handler(worker)
                    .timeout(DEFAULT_TIMEOUT)
                    .maxJobsActive(DEFAULT_MAX_JOBS_ACTIVE)
                    .open();
            openWorker.add(opened);
            log.info("Registered job worker for jobType={}", worker.jobType());
        }
    }

    @PreDestroy
    void closeWorkers() {
        for(JobWorker worker : openWorker) {
            worker.close();
        }
        log.info("Closed all job workers");
    }
}
