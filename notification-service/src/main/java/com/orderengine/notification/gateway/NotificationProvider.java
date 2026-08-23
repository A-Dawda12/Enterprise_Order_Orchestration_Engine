package com.orderengine.notification.gateway;

import java.util.Map;

public interface NotificationProvider {

    void send(String notificationId, String recipient, String template, Map<String, String> variables);
}
