package com.orderengine.order.client.orchestration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.orderengine.client.orchestration")
public class OrchestrationClientProperties {

    private String baseUrl = "http://localhost:8090/orchestration";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
