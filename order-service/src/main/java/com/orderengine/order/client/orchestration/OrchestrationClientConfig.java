package com.orderengine.order.client.orchestration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.common.http.DomainServiceRestClients;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(OrchestrationClientProperties.class)
public class OrchestrationClientConfig {

    static final String OECHESTRATION_REST_CLIENT = "orchestrationRestClient";

    @Bean(name = OECHESTRATION_REST_CLIENT)
    RestClient orchestrationRestClient(
            ObjectProvider<RestClient.Builder> restClientProvider,
            OrchestrationClientProperties properties,
            ObjectMapper objectMapper) {
        RestClient.Builder builder = restClientProvider.getIfAvailable(RestClient::builder);
        return DomainServiceRestClients.restClient(
                builder, "order-orchestartion-app", properties.getBaseUrl(), objectMapper
        );
    }

    @Bean
    OrchestrationApi orchestrationApi(@Qualifier(OECHESTRATION_REST_CLIENT) RestClient orchestrationRestClient) {
        return DomainServiceRestClients.proxy(orchestrationRestClient, OrchestrationApi.class);
    }
}
