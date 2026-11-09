package com.orderengine.order.client.paymentmethod;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class MockPaymentMethodTokenProvider implements PaymentMethodTokenProvider {

    static final String TOKEN_PREFIX = "pm_tok_";

    @Override
    public String issueToken(String orderId, String customerId) {
        String digest = UUID.nameUUIDFromBytes((customerId + ":" + orderId).getBytes(StandardCharsets.UTF_8))
                .toString().replace("-", "").substring(0, 16);
        return TOKEN_PREFIX + orderId.replace("-", "").substring(0, 8) + "-" + digest;
    }
}
