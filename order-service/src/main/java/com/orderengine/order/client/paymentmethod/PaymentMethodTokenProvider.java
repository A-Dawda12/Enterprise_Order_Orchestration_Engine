package com.orderengine.order.client.paymentmethod;

public interface PaymentMethodTokenProvider {

    String issueToken(String orderId, String customerId);
}
