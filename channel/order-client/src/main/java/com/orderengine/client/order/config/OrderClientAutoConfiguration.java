package com.orderengine.client.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.client.order.OrderServiceClient;
import com.orderengine.client.order.api.OrdersApi;
import com.orderengine.common.http.DomainServiceRestClients;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@ConditionalOnClass(OrdersApi.class)
@EnableConfigurationProperties(OrderClientProperties.class)
@Import(OrderServiceClient.class)
public class OrderClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "orderServiceRestClient")
    RestClient orderServiceRestClient(
            RestClient.Builder restClientBuilder,
            OrderClientProperties properties,
            ObjectMapper objectMapper
    ) {
        return DomainServiceRestClients.restClient(
                restClientBuilder,
                "order-service",
                properties.getBaseUrl(),
                objectMapper
        );
    }

    @Bean
    @ConditionalOnMissingBean
    OrdersApi ordersApi(RestClient orderServiceRestClient) {
        return DomainServiceRestClients.proxy(orderServiceRestClient, OrdersApi.class);
    }
}
