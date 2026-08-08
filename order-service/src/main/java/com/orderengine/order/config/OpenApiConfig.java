package com.orderengine.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI orderServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .description("Order Service API documentation")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("/orders").description("Local (servlet context-path)")
                ));
    }


    @Bean
    OperationCustomizer idempotencyKeyCustomizer() {
        return (operation, handlerMethod) -> {
            operation.addParametersItem(
                    new io.swagger.v3.oas.models.parameters.Parameter()
                            .name("Idempotency-Key")
                            .description("Idempotency key for request deduplication")
                            .in("header")
                            .required(false)
                            .schema(new io.swagger.v3.oas.models.media.Schema<>().type("string"))
            );
            return operation;
        };
    }
}
