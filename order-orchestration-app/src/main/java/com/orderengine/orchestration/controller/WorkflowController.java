package com.orderengine.orchestration.controller;

import com.orderengine.common.orchestration.ActivityParameters;
import com.orderengine.common.orchestration.HandlerActivityName;
import com.orderengine.orchestration.api.dto.StartWorkflowRequest;
import com.orderengine.orchestration.api.dto.WorkflowInstanceResponse;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class WorkflowController {

    private final ZeebeClient zeebeClient;

    WorkflowController(ZeebeClient zeebeClient){
        this.zeebeClient = zeebeClient;
    }

    @PostMapping("/processes/order-fulfillment/start")
    ResponseEntity<WorkflowInstanceResponse> start(@Valid @RequestBody StartWorkflowRequest request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(ActivityParameters.ORDER_ID, request.orderId().toString());
        variables.put(ActivityParameters.PAYMENT_METHOD_TOKEN, request.paymentMethodToken());

        ProcessInstanceEvent instance = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(HandlerActivityName.PROCESS_ORDER_FULLFILLMENT)
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        WorkflowInstanceResponse response = new WorkflowInstanceResponse(
                String.valueOf(instance.getProcessInstanceKey()),
                instance.getBpmnProcessId(),
                instance.getVersion(),
                request.orderId(),
                "ACTIVE",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
