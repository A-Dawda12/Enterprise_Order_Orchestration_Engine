package com.orderengine.orchestration.config;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.DeployResourceCommandStep1;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class ZeebeDeploymentConfig {

    private static final String BPMN_CLASSPATH_PATTERN = "classpath*:bpmn/*.bpmn";

    private final ZeebeClient zeebeClient;

    ZeebeDeploymentConfig(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    void deployProcessDefination() throws IOException {
        Resource[] resources = new org.springframework.core.io.support.PathMatchingResourcePatternResolver()
                .getResources(BPMN_CLASSPATH_PATTERN);

        if(resources.length == 0){
            log.warn("No BPMN resources found on classpath at '{}' - nothing deployed", BPMN_CLASSPATH_PATTERN);
            return;
        }

        DeployResourceCommandStep1 step1 = zeebeClient.newDeployResourceCommand();
        DeployResourceCommandStep1.DeployResourceCommandStep2 step2 = null;
        for(Resource resource : resources) {
            step2 = (step2 == null)
                    ? step1.addResourceStream(resource.getInputStream(), resource.getFilename())
                    : step2.addResourceStream(resource.getInputStream(), resource.getFilename());
        }

        DeploymentEvent deployment = step2.send().join();
        deployment.getProcesses().forEach(process -> log.info("Deployed bpmn process: {} with version: {}, processDefintionKey={}",
                process.getBpmnProcessId(), process.getVersion(), process.getProcessDefinitionKey()));
    }
}
