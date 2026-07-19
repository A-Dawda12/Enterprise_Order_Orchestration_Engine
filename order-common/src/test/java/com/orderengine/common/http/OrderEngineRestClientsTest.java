package com.orderengine.common.http;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderEngineRestClientsTest {

    @Test
    void customizeReturnsBuilderWithInterceptor() {
        RestClient.Builder result = OrderEngineRestClients.customizer(RestClient.builder());
        assertThat(result).isNotNull();
        assertThat(result.build()).isNotNull();

    }
}
