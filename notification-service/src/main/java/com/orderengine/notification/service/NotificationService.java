package com.orderengine.notification.service;

import com.orderengine.notification.domain.NotificationChannel;
import com.orderengine.notification.domain.NotificationEntity;
import com.orderengine.notification.domain.NotificationTemplate;

import java.util.Map;

public interface NotificationService {

    NotificationEntity send(
            String orderId,
            String customerId,
            NotificationChannel channel,
            NotificationTemplate template,
            Map<String, String> variables
    );

    NotificationEntity getNotification(String notificationId);
}
