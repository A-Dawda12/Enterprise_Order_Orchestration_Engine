package com.orderengine.common.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.common.filter.IdempotencyFilter;
import com.orderengine.common.web.ApiErrorResponseWriter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(StringRedisTemplate.class)
@ConditionalOnBean(StringRedisTemplate.class)
@ConditionalOnProperty(prefix = "orderengine.idempotency", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(IdempotencyProperties.class)
public class IdempotencyAutoConfiguration {

    @Bean
    IdempotencyResponseStore idempotencyResponseStore(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            IdempotencyProperties properties,
            Environment environment
    ){
        String serviceName = environment.getProperty(
                "spring.application.name",
                "order-engine"
        );
        return new IdempotencyResponseStore(
                redisTemplate,
                objectMapper,
                serviceName,
                properties.getTtl()
        );
    }

    @Bean
    IdempotencyFilter idempotencyFilter(
            IdempotencyResponseStore idempotencyResponseStore,
            ApiErrorResponseWriter apiErrorResponseWriter
    ){
        return new IdempotencyFilter(idempotencyResponseStore, apiErrorResponseWriter);
    }

}
