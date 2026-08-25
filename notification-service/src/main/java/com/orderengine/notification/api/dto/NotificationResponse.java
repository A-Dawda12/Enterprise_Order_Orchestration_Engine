package com.orderengine.notification.api.dto;

import java.time.Instant;

public record NotificationResponse(
        String notificationId,
        String status,
        String channel,
        String recipient,
        Instant queuedAt
) {
}
