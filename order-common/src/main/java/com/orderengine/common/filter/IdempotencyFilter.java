package com.orderengine.common.filter;

import com.orderengine.common.OrderEngineConstants;
import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import com.orderengine.common.idempotency.CachedResponse;
import com.orderengine.common.idempotency.IdempotencyResponseStore;
import com.orderengine.common.web.ApiErrorResponseWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final Set<String> MUTATING_METHODS = Set.of("POST", "PUT", "PATCH");

    private final IdempotencyResponseStore idempotencyResponseStore;
    private final ApiErrorResponseWriter errorResponseWriter;

    public IdempotencyFilter(
            IdempotencyResponseStore idempotencyResponseStore,
            ApiErrorResponseWriter errorResponseWriter
    ) {
        this.idempotencyResponseStore = idempotencyResponseStore;
        this.errorResponseWriter = errorResponseWriter;
    }

    @Override
    protected  boolean shouldNotFilter(HttpServletRequest request) {
        return !MUTATING_METHODS.contains(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String idempotencyKey = request.getHeader(OrderEngineConstants.IDEMPOTENCY_KEY_HEADER);
        if(idempotencyKey == null || idempotencyKey.isBlank()) {
            errorResponseWriter.write(
                    response,
                    ErrorCode.BAD_REQUEST,
                    "Missing required header: " + OrderEngineConstants.IDEMPOTENCY_KEY_HEADER,
                    request.getRequestURI()
            );
            return;
        }
        try{
            handleIdempotencyRequest(request, response, filterChain, idempotencyKey);
        } catch (OrderEngineException ex) {
            errorResponseWriter.write(
                    response,
                    ex,
                    request.getRequestURI()
            );
        }
    }

    private void handleIdempotencyRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain,
            String idempotencyKey
    ) throws IOException, ServletException {
        var cached = idempotencyResponseStore.find(idempotencyKey);
        if(cached.isPresent()){
            writeCachedResponse(response, cached.get());
            return;
        }

        if(!idempotencyResponseStore.tryAcquire(idempotencyKey)) {
            if(idempotencyResponseStore.tryAcquire(idempotencyKey)) {
                throw new OrderEngineException(
                        ErrorCode.CONFLICT,
                        "Request with this idempotency key is already in progress"
                );
            }

            cached = idempotencyResponseStore.find(idempotencyKey);
            if(cached.isPresent()) {
                writeCachedResponse(response, cached.get());
                return;
            }
            throw new OrderEngineException(
                    ErrorCode.CONFLICT,
                    "Request with this idempotency key is already in progress"
            );
        }

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        try{
            filterChain.doFilter(request, responseWrapper);
            if(responseWrapper.getStatus() >= 200 && responseWrapper.getStatus() < 300) {
                idempotencyResponseStore.save(idempotencyKey, toCachedResponse(responseWrapper));
            }
            else{
                idempotencyResponseStore.release(idempotencyKey);
            }
            responseWrapper.copyBodyToResponse();
        } catch (Exception ex) {
            idempotencyResponseStore.release(idempotencyKey);
            throw ex;
        }
    }

    private CachedResponse toCachedResponse(ContentCachingResponseWrapper response) {
        String contentType = response.getContentType();
        if(contentType == null) {
            contentType = MediaType.APPLICATION_JSON_VALUE;
        }
        String body = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        return new CachedResponse(response.getStatus(), contentType, body);
    }

    private void writeCachedResponse(HttpServletResponse response, CachedResponse cached) throws IOException {
        response.setStatus(cached.status());
        response.setContentType(cached.contentType());
        response.getWriter().write(cached.body());
    }
}
