package com.orderengine.notification.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record SendNotificationRequest(
        @NotBlank String orderId,
        @NotBlank String customerId,
        @NotBlank String channel,
        @NotBlank String template,
        Map<String, String> variables
) {
}
