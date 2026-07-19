package com.orderengine.common.error;

import com.orderengine.common.OrderEngineConstants;

import java.util.List;

public class OrderEngineException extends RuntimeException{

    private final ErrorCode errorCode;
    private final List<ErrorDetail> details;

    public OrderEngineException(ErrorCode errorCode, String message) {
        this(errorCode, message, List.of());
    }

    public OrderEngineException(ErrorCode errorCode, String message, List<ErrorDetail> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public ErrorCode errorCode() {
        return errorCode;
    }

    public List<ErrorDetail> details() {
        return details;
    }
}
