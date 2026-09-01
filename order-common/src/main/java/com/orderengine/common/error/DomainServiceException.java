package com.orderengine.common.error;

public class DomainServiceException extends OrderEngineException {

    private final String serviceName;
    private final int httpStatus;

    public DomainServiceException(String serviceName, int httpStatus, ErrorCode errorCode, String message){
        super(errorCode, message);
        this.serviceName = serviceName;
        this.httpStatus = httpStatus;
    }

    public String ServiceName() {
        return serviceName;
    }

    public int httpStatus() {
        return httpStatus;
    }

    public static ErrorCode errorCodeForStatus(int status) {
        return switch (status) {
            case 400 -> ErrorCode.BAD_REQUEST;
            case 404 -> ErrorCode.NOT_FOUND;
            case 409 -> ErrorCode.CONFLICT;
            case 503 -> ErrorCode.SERVICE_UNAVAILABLE;
            default -> status >= 500 ? ErrorCode.INTERNAL_ERROR : ErrorCode.BAD_REQUEST;

        };
    }
}
