package com.orderengine.notification.api.mapper;

import com.orderengine.notification.api.dto.NotificationResponse;
import com.orderengine.notification.domain.NotificationEntity;

public final class NotificationApiMapper {

    private NotificationApiMapper() {}

    public static NotificationResponse toResponse(NotificationEntity entity ) {
        return new NotificationResponse(
                entity.getNotificationId(),
                entity.getStatus().name(),
                entity.getChannel().name(),
                maskRecipient(entity.getRecipient()),
                entity.getQueuedAt()
        );
    }

    static String maskRecipient(String recipient) {
        if(recipient == null || recipient.isBlank()) {
            return recipient;
        }
        int at = recipient.indexOf('@');
        if(at <= 0) {
            return "***";
        }
        return recipient.charAt(0) + "***" + recipient.substring(at);
    }
}
