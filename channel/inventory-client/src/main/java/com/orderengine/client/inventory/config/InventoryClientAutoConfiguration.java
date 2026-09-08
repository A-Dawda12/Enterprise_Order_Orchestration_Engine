package com.orderengine.client.inventory.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.client.inventory.InventoryServiceClient;
import com.orderengine.client.inventory.api.InventoryApi;
import com.orderengine.common.http.DomainServiceRestClients;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@ConditionalOnClass(InventoryApi.class)
@EnableConfigurationProperties(InventoryClientProperties.class)
@Import(InventoryServiceClient.class)
public class InventoryClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "inventoryServiceRestClient")
    RestClient inventoryServiceRestClient(
            RestClient.Builder restClientBuilder,
            InventoryClientProperties properties,
            ObjectMapper objectMapper
    ) {
        return DomainServiceRestClients.restClient(
                restClientBuilder,
                "inventory-service",
                properties.getBaseUrl(),
                objectMapper
        );
    }

    @Bean
    @ConditionalOnMissingBean
    InventoryApi inventoryApi(RestClient inventoryServiceRestClient) {
        return DomainServiceRestClients.proxy(
                inventoryServiceRestClient,
                InventoryApi.class
        );
    }
}