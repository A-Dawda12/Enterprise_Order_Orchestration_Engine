package com.orderengine.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.common.idempotency.CachedResponse;
import com.orderengine.common.idempotency.IdempotencyResponseStore;
import com.orderengine.common.web.ApiErrorResponseWriter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IdempotencyFilterTest {

    @Mock
    private IdempotencyResponseStore store;

    @Mock
    private FilterChain filterChain;

    private IdempotencyFilter filter;

    private ApiErrorResponseWriter errorResponseWriter;

    @BeforeEach
    void setUp() {
        errorResponseWriter = new ApiErrorResponseWriter((new ObjectMapper()));
        filter = new IdempotencyFilter(store, errorResponseWriter);
    }

    @Test
    void returnsCachedResponseForDuplicateKey() throws Exception {
        when(store.find("key-1")).thenReturn(Optional.of(
                new CachedResponse(201, MediaType.APPLICATION_JSON_VALUE, "{\"orderId\":\"ord-1\"}")
        ));

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/v1/orders");
        request.addHeader("Idempotency-Key", "key-1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getContentAsString()).isEqualTo("{\"orderId\":\"ord-1\"}");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void skipsNonMutatingRequests() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/orders");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(store, never()).find(any());
    }

}