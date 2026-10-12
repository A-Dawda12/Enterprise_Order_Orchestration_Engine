package com.orderengine.client.notification;


import com.orderengine.client.notification.api.NotificationsApi;
import com.orderengine.client.notification.model.NotificationResponse;
import com.orderengine.client.notification.model.SendNotificationRequest;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceClient {

    private final NotificationsApi notificationsApi;

    public NotificationServiceClient(NotificationsApi notificationsApi) {
        this.notificationsApi = notificationsApi;
    }

    public NotificationResponse send(String idempotencyKey, SendNotificationRequest request) {
        return notificationsApi.send(idempotencyKey, request);
    }

    public NotificationResponse getNotification(String notificationId) {
        return notificationsApi.getNotification(notificationId);
    }
}