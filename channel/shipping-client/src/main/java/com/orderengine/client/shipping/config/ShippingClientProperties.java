package com.orderengine.client.shipping.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.orderengine.client.shipping")
public class ShippingClientProperties {

    private String baseUrl = "http://localhost:8085/shipping";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}