package com.orderengine.common.http;

import com.orderengine.common.OrderEngineConstants;
import com.orderengine.common.logging.MdcKeys;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class CorrelationIdClientInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {
        String correlationId = MDC.get(MdcKeys.CORRELATION_ID);
        if (correlationId != null && !correlationId.isBlank()
        && !request.getHeaders().containsHeader(OrderEngineConstants.CORRELATION_ID_HEADER)) {
            request.getHeaders().set(OrderEngineConstants.CORRELATION_ID_HEADER, correlationId);
        }
        return execution.execute(request, body);
    }
}
