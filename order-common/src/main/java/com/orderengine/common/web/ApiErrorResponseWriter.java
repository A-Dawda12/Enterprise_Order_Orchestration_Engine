package com.orderengine.common.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderengine.common.error.ApiErrorResponse;
import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.ErrorDetail;
import com.orderengine.common.error.OrderEngineException;
import com.orderengine.common.logging.MdcKeys;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ApiErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public ApiErrorResponseWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void write(HttpServletResponse response, OrderEngineException ex, String path) throws IOException {
       write(response, ex.errorCode(), ex.getMessage(), path, ex.details());
    }

    public void write(
            HttpServletResponse response,
            ErrorCode errorCode,
            String message,
            String path
    ) throws IOException {
        write(response, errorCode, message, path, List.of());
    }

    private void write(
            HttpServletResponse response,
            ErrorCode errorCode,
            String message,
            String path,
            List<ErrorDetail> details
    ) throws IOException {
        ApiErrorResponse body = new ApiErrorResponse(
                java.time.Instant.now(),
                errorCode.status().value(),
                errorCode.name(),
                message,
                path,
                response.getHeader(MdcKeys.CORRELATION_ID),
                details
        );

        response.setStatus(errorCode.status().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), body);
    }
}
