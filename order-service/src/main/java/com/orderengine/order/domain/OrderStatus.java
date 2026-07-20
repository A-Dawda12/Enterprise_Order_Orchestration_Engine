package com.orderengine.order.domain;

public enum OrderStatus {
    CREATED,
    VALIDATED,
    PROCESSING,
    PAYMENT_PENDING,
    FRAUD_REVIEW,
    PAID,
    SHIPPED,
    COMPLETED,
    COMPENSATING,
    CANCELLED,
    FAILED,
    PAYMENT_TIMEOUT,
    REJECTED
}
