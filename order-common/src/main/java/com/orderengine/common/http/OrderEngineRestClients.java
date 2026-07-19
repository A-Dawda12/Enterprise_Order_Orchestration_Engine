 package com.orderengine.common.http;

import org.springframework.web.client.RestClient;

public final class OrderEngineRestClients {

    private OrderEngineRestClients() {

    }

    public static RestClient.Builder customizer(RestClient.Builder builder) {
        return builder.requestInterceptor(new CorrelationIdClientInterceptor());
    }
}
