package com.orderengine.common.web;

import com.orderengine.common.error.ApiErrorResponse;
import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.ErrorDetail;
import com.orderengine.common.error.OrderEngineException;
import com.orderengine.common.logging.MdcKeys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderEngineException.class)
    public ResponseEntity<ApiErrorResponse> handleOrderEngineException(
            OrderEngineException ex,
            HttpServletRequest request
    ) {
        return buildResponse(ex.errorCode(), ex.getMessage(), request.getRequestURI(), ex.details());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return buildResponse(
                ErrorCode.VALIDATION_ERROR,
                "Validation failed",
                request.getRequestURI(),
                details
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnecpectedException(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                ErrorCode.INTERNAL_ERROR,
                "An unexpected error occurred",
                request.getRequestURI(),
                List.of()
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            ErrorCode errorCode,
            String message,
            String path,
            List<ErrorDetail> details) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                errorCode.status().value(),
                errorCode.name(),
                message,
                path,
                MDC.get(MdcKeys.CORRELATION_ID),
                details
        );
        return ResponseEntity.status(errorCode.status()).body(response);
    }
}
