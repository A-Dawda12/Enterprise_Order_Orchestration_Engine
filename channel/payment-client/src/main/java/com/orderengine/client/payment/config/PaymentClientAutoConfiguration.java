package com.orderengine.client.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.client.payment.PaymentServiceClient;
import com.orderengine.client.payment.api.PaymentsApi;
import com.orderengine.common.http.DomainServiceRestClients;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@ConditionalOnClass(PaymentsApi.class)
@EnableConfigurationProperties(PaymentClientProperties.class)
@Import(PaymentServiceClient.class)
public class PaymentClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "paymentServiceRestClient")
    RestClient paymentServiceRestClient(
            RestClient.Builder restClientBuilder,
            PaymentClientProperties properties,
            ObjectMapper objectMapper
    ) {
        return DomainServiceRestClients.restClient(
                restClientBuilder,
                "payment-service",
                properties.getBaseUrl(),
                objectMapper
        );
    }

    @Bean
    @ConditionalOnMissingBean
    PaymentsApi paymentsApi(RestClient paymentServiceRestClient) {
        return DomainServiceRestClients.proxy(
                paymentServiceRestClient,
                PaymentsApi.class
        );
    }
}