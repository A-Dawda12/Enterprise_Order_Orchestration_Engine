package com.orderengine.common.http;

import com.orderengine.common.OrderEngineConstants;
import com.orderengine.common.logging.MdcKeys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.net.URI;

import static org.springframework.http.HttpStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;

public class CorrelationIdClientInterceptorTest {

    private final CorrelationIdClientInterceptor interceptor = new CorrelationIdClientInterceptor();

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void copiesCorrelationIdFromMdcOntoOutboundRequest() throws  Exception {
        MDC.put(MdcKeys.CORRELATION_ID, "corr-outbound-1");
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://localhost/orders/v1/health"));
        ClientHttpRequestExecution execution =  (req, body) -> new MockClientHttpResponse(new byte[0], OK);
        interceptor.intercept(request, new byte[0], execution);

        assertThat(request.getHeaders().getFirst(OrderEngineConstants.CORRELATION_ID_HEADER)).
                isEqualTo("corr-outbound-1");
    }

    @Test
    void doesNotOverrrideExistingCorrelationIdHeader() throws Exception {
        MDC.put(MdcKeys.CORRELATION_ID, "from-mdc");
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://localhost/test"));
        request.getHeaders().set(OrderEngineConstants.CORRELATION_ID_HEADER, "from-request");
        ClientHttpRequestExecution execution =  (req, body) -> new MockClientHttpResponse(new byte[0], OK);
        interceptor.intercept(request, new byte[0], execution);

        assertThat(request.getHeaders().getFirst(OrderEngineConstants.CORRELATION_ID_HEADER)).
                isEqualTo("from-request");
    }
}
