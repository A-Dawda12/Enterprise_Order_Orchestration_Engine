package com.orderengine.common.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.common.error.DomainServiceException;
import com.orderengine.common.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class DomainServiceRestClientsTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper().findAndRegisterModules();

    @Test
    void mapsApiErrorResponseBody() throws Exception {
        String json = """
                {
                  "timestamp": "2026-09-01T12:00:00+05:30",
                  "status": 404,
                  "error": "Not Found",
                  "message": "Order not found",
                  "path": "/v1/orders/abc",
                  "correlationId": "corr-1",
                  "details": []
                }
                """;

        ClientHttpResponse response = new MockClientHttpResponse(
                json.getBytes(StandardCharsets.UTF_8),
                HttpStatus.NOT_FOUND
        );

        DomainServiceException ex = DomainServiceRestClients.toException(
                "order-service",
                response,
                objectMapper
        );

        assertThat(ex.serviceName()).isEqualTo("order-service");
        assertThat(ex.httpStatus()).isEqualTo(404);
        assertThat(ex.errorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        assertThat(ex.getMessage()).isEqualTo("Order not found");
    }
}
