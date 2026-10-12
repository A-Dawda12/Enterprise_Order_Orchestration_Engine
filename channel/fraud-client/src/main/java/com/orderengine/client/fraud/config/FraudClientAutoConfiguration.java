package com.orderengine.client.fraud.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.client.fraud.FraudServiceClient;
import com.orderengine.client.fraud.api.FraudApi;
import com.orderengine.common.http.DomainServiceRestClients;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@ConditionalOnClass(FraudApi.class)
@EnableConfigurationProperties(FraudClientProperties.class)
@Import(FraudServiceClient.class)
public class FraudClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "fraudServiceRestClient")
    RestClient fraudServiceRestClient(
            RestClient.Builder restClientBuilder,
            FraudClientProperties properties,
            ObjectMapper objectMapper
    ) {
        return DomainServiceRestClients.restClient(
                restClientBuilder,
                "fraud-service",
                properties.getBaseUrl(),
                objectMapper
        );
    }

    @Bean
    @ConditionalOnMissingBean
    FraudApi fraudApi(RestClient fraudServiceRestClient) {
        return DomainServiceRestClients.proxy(
                fraudServiceRestClient,
                FraudApi.class
        );
    }
}