package com.orderengine.fraud.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record FraudAssessmentResponse(
        String assessmentId,
        String orderId,
        int score,
        String riskLevel,
        boolean requiresReview,
        List<String> signals,
        Instant assessedAt
) {
}
