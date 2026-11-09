package com.orderengine.orchestration.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartWorkflowRequest(@NotNull UUID orderId, @NotNull String paymentMethodToken) {
}
