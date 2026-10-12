package com.orderengine.client.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.client.notification.NotificationServiceClient;
import com.orderengine.client.notification.api.NotificationsApi;
import com.orderengine.common.http.DomainServiceRestClients;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@ConditionalOnClass(NotificationsApi.class)
@EnableConfigurationProperties(NotificationClientProperties.class)
@Import(NotificationServiceClient.class)
public class NotificationClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "notificationServiceRestClient")
    RestClient notificationServiceRestClient(
            RestClient.Builder restClientBuilder,
            NotificationClientProperties properties,
            ObjectMapper objectMapper
    ) {
        return DomainServiceRestClients.restClient(
                restClientBuilder,
                "notification-service",
                properties.getBaseUrl(),
                objectMapper
        );
    }

    @Bean
    @ConditionalOnMissingBean
    NotificationsApi notificationsApi(RestClient paymentServiceRestClient) {
        return DomainServiceRestClients.proxy(
                paymentServiceRestClient,
                NotificationsApi.class
        );
    }
}