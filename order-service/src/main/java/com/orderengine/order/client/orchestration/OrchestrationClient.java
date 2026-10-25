package com.orderengine.order.client.orchestration;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrchestrationClient {

    private final OrchestrationApi orchestrationApi;

    public OrchestrationClient(OrchestrationApi orchestrationApi){
        this.orchestrationApi = orchestrationApi;
    }

    public WorkflowInstanceResponse startOrderFulfillment(UUID orderId) {
        return orchestrationApi.startOrderFulFillment(new StartWorkflowRequest(orderId));
    }
}
