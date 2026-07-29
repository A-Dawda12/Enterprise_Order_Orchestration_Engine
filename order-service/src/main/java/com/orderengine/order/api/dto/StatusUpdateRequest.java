package com.orderengine.order.api.dto;

import com.orderengine.order.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull OrderStatus status,
        String reason
) {
}
