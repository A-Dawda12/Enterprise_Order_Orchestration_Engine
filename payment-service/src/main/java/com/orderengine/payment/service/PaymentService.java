package com.orderengine.payment.service;

import com.orderengine.payment.domain.PaymentEntity;
import com.orderengine.payment.domain.RefundEntity;

import java.math.BigDecimal;

public interface PaymentService {

    PaymentEntity authorize(
            String orderId,
            String customerId,
            BigDecimal amount,
            String currency,
            String paymentMethodToken
    );

    PaymentEntity getPayment(String paymentId);

    PaymentEntity capture(String paymentId, BigDecimal amount);

    RefundEntity refund(String paymentId, String reason, BigDecimal amount);

    PaymentEntity voidAuthorization(String paymentId);
}
