package com.orderengine.common.http;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@ConditionalOnClass(RestClient.class)
public class  TracingClientAutoConfiguration {

    @Bean
    CorrelationIdClientInterceptor correlationIdClientInterceptor() {
        return new CorrelationIdClientInterceptor();
    }
}
