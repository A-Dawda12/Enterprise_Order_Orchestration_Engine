package com.orderengine.orchestration.config;

import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZeebeClientConfig {

    @Bean(destroyMethod = "close")
    ZeebeClient zeebeClient(
            @Value("${zeebe.client.broker.contactPoint:http://localhost:26500}") String grpcAddress,
            @Value("${zeebe.client.broker.rest-address:http://localhost:8080}") String restAddress
    ) {
        return ZeebeClient.newClientBuilder()
                .grpcAddress(java.net.URI.create(grpcAddress))
                .restAddress(java.net.URI.create(restAddress))
                .usePlaintext()
                .build();
    }
}
