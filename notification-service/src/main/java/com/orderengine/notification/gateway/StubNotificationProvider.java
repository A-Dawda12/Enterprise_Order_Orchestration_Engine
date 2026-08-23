package com.orderengine.notification.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class StubNotificationProvider implements NotificationProvider{

    @Override
    public void send(String notificationId, String recipient, String template, Map<String, String> variables) {
        log.info("stub send notificationId={} recepient={} template={} variable={}",
                notificationId, recipient, template, variables);
    }
}
