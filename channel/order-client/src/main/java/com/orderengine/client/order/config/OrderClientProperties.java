package com.orderengine.client.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.orderengine.client.order")
public class OrderClientProperties {

    private String baseUrl = "http://localhost:8081/orders";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
