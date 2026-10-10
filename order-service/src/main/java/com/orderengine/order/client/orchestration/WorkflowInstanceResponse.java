package com.orderengine.order.client.orchestration;

import java.time.Instant;
import java.util.UUID;

public record WorkflowInstanceResponse(
        String instanceKey,
        String processDefinitionId,
        int processDefinitionVersion,
        UUID orderId,
        String status,
        Instant startAt
) {
}
