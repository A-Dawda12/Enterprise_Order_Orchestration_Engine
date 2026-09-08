package com.orderengine.common.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.common.error.ApiErrorResponse;
import com.orderengine.common.error.DomainServiceException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class DomainServiceRestClients {

    private DomainServiceRestClients(){
    }

    public static RestClient.Builder customize(
            RestClient.Builder builder,
            String serviceName,
            ObjectMapper objectMapper
    ) {
        return OrderEngineRestClients.customizer(builder)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    throw toException(serviceName, response, objectMapper);
                });
    }

    public static RestClient restClient(
            RestClient.Builder builder,
            String serviceName,
            String baseUrl,
            ObjectMapper objectMapper
    ) {
        return customize(builder, serviceName, objectMapper)
                .baseUrl(baseUrl)
                .build();
    }

    public static <T> T proxy(RestClient restClient, Class<T> apiType) {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(apiType);
    }

    public static DomainServiceException toException(
            String serviceName,
            ClientHttpResponse response,
            ObjectMapper objectMapper
    ) throws IOException {
        int status = response.getStatusCode().value();
        String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        return new DomainServiceException(serviceName,
                status,
                DomainServiceException.errorCodeForStatus(status),
                extractMessage(body, status, objectMapper)
        );
    }

    private static String extractMessage(String body, int status, ObjectMapper objectMapper) {
        if(body == null || body.isBlank()) {
            return "Domain service returned HTTP " + status;
        }
        try {
            ApiErrorResponse error = objectMapper.readValue(body, ApiErrorResponse.class);
            if(error.message() != null&&  !error.message().isBlank()) {
                return error.message();
            }
        } catch (Exception e) {
            return "Domain service returned HTTP " + status;
        }
        return body.length() > 500 ? body.substring(0, 500) + "..." : body;
    }
}
