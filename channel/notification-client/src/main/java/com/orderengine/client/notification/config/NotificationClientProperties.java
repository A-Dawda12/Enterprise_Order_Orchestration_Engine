package com.orderengine.client.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.orderengine.client.notification")
public class NotificationClientProperties {

    private String baseUrl = "http://localhost:8086/notifications";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}