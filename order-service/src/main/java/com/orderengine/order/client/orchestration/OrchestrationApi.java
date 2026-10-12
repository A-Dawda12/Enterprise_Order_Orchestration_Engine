package com.orderengine.order.client.orchestration;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface OrchestrationApi {

    @PostExchange("/v1/processes/order-fulfillment/start")
    WorkflowInstanceResponse startOrderFulFillment(@RequestBody StartWorkflowRequest request);
}
