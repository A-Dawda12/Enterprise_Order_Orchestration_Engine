package com.orderengine.payment.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class StubPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(StubPaymentGateway.class);

    @Override
    public String authorize(
            String orderId,
            BigDecimal amount,
            String currency,
            String paymentMethodToken
    ) {
        String gatewayRef = "gw_" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 16);

        log.info(
                "stub authorize orderId={} amount={} {} token={} → gatewayRef={}",
                orderId,
                amount,
                currency,
                paymentMethodToken,
                gatewayRef
        );

        return gatewayRef;
    }

    @Override
    public void capture(String gatewayRef, BigDecimal amount) {
        log.info(
                "stub capture gatewayRef={} amount={}",
                gatewayRef,
                amount
        );
    }

    @Override
    public void refund(String gatewayRef, BigDecimal amount) {
        log.info(
                "stub refund gatewayRef={} amount={}",
                gatewayRef,
                amount
        );
    }

    @Override
    public void voidAuthorization(String gatewayRef) {
        log.info(
                "stub void gatewayRef={}",
                gatewayRef
        );
    }
}