package com.orderengine.orchestration.api.dto;

import java.time.Instant;
import java.util.UUID;

public record WorkflowInstanceResponse(
        String idempotencyKey,
        String processDefinitionId,
        int processDefinitionVersion,
        UUID orderId,
        String status,
        Instant startedAt
) {
}
