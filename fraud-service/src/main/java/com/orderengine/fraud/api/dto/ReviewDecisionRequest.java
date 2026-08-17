package com.orderengine.fraud.api.dto;

import com.orderengine.fraud.domain.ReviewDecisionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewDecisionRequest(
        @NotNull ReviewDecisionType decision,
        @NotBlank String reviewerId,
        String notes
) {
}
