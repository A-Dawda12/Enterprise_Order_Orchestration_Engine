package com.orderengine.client.shipping.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.client.shipping.ShippingServiceClient;
import com.orderengine.client.shipping.api.ShippingApi;
import com.orderengine.common.http.DomainServiceRestClients;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@ConditionalOnClass(ShippingApi.class)
@EnableConfigurationProperties(ShippingClientProperties.class)
@Import(ShippingServiceClient.class)
public class ShippingClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "shippingServiceRestClient")
    RestClient shippingServiceRestClient(
            RestClient.Builder restClientBuilder,
            ShippingClientProperties properties,
            ObjectMapper objectMapper
    ) {
        return DomainServiceRestClients.restClient(
                restClientBuilder,
                "shipping-service",
                properties.getBaseUrl(),
                objectMapper
        );
    }

    @Bean
    @ConditionalOnMissingBean
    ShippingApi shippingApi(RestClient shippingServiceRestClient) {
        return DomainServiceRestClients.proxy(
                shippingServiceRestClient,
                ShippingApi.class
        );
    }
}