package com.orderengine.order.client.paymentmethod;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MockPaymentMethodTokenProviderTest {

    private final MockPaymentMethodTokenProvider provider = new MockPaymentMethodTokenProvider();

    @Test
    void issueTokenUsesExpectedFormat() {
        String orderId = "5407bfbb-39f4-434a-87be-3f84961c1a3e";
        String token = provider.issueToken(orderId, "cust-1");

        assertThat(token).startsWith(MockPaymentMethodTokenProvider.TOKEN_PREFIX);
        assertThat(token).matches("pm_tok_[0-9a-f]{8}_[0-9a-f]{16}");
        assertThat(token).contains("5407bfbb");
        assertThat(token).doesNotContain("cust-1");
    }

    @Test
    void sameOrderAndCustomerAlwaysProduceSameToken() {
        String orderId = "11111111-1111-1111-1111-111111111111";
        assertThat(provider.issueToken(orderId, "cust-a"))
                .isEqualTo(provider.issueToken(orderId, "cust-a"));
    }

    @Test
    void differentCustomerProducesDifferentToken() {
        String orderId = "11111111-1111-1111-1111-111111111111";
        assertThat(provider.issueToken(orderId, "cust-a"))
                .isNotEqualTo(provider.issueToken(orderId, "cust-b"));
    }
}
