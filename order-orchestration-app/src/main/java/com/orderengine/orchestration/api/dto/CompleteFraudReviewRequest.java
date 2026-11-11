package com.orderengine.orchestration.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CompleteFraudReviewRequest(
        @NotNull UUID orderId,
        @NotBlank String decision,
        String reviewerId,
        String notes
) {
}
