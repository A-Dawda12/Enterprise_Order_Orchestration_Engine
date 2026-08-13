package com.orderengine.payment.gateway;

import java.math.BigDecimal;

public interface PaymentGateway {

    String authorize(String orderId, BigDecimal amount, String currency, String paymentMethodToken);

    void capture(String gatewayRef, BigDecimal amount);

    void refund(String gatewayRef, BigDecimal amount);

    void voidAuthorization(String gatewayRef);
}
