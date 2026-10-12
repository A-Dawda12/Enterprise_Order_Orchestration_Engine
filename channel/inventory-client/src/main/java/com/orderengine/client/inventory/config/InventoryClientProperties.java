package com.orderengine.client.inventory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.orderengine.client.inventory")
public class InventoryClientProperties {

    private String baseUrl = "http://localhost:8082/inventory";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}